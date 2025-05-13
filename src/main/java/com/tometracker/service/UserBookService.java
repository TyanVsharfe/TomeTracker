package com.tometracker.service;

import com.tometracker.data_template.Enums;
import com.tometracker.db.model.*;
import com.tometracker.db.repository.BookRepository;
import com.tometracker.db.repository.ReadingGoalRepository;
import com.tometracker.db.repository.UserBookRepository;
import com.tometracker.db.repository.UserRepository;
import com.tometracker.dto.UserBookDTO;
import com.tometracker.dto.UserBookUpdateDTO;
import com.tometracker.data_template.UserInfo;
import com.tometracker.dto.UserReviewsDTO;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserBookService {
    private final UserBookRepository userBookRepository;
    private final BookRepository bookRepository;
    private final UserRepository userRepository;
    private final AchievementProcessorService achievementProcessorService;
    private final ReadingGoalRepository readingGoalRepository;

    public UserBookService(UserBookRepository userBookRepository, BookRepository bookRepository, UserRepository userRepository, AchievementProcessorService achievementProcessorService, ReadingGoalRepository readingGoalRepository) {
        this.userBookRepository = userBookRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
        this.achievementProcessorService = achievementProcessorService;
        this.readingGoalRepository = readingGoalRepository;
    }

    public Iterable<UserBook> getAll(String status) {
        if (status == null || status.isEmpty()) {
            return userBookRepository.findAll();
        }
        else {
            try {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                System.out.println("Authentication in /get: " + authentication.getName());
                Optional<User> author = userRepository.findByUsername(authentication.getName());
                if (author.isEmpty())
                    throw new UsernameNotFoundException("");

                Enums.status statusEnum = Enums.status.valueOf(status);
                return userBookRepository.findBooksByStatusAndUser_Username(statusEnum, author.get().getUsername());
            }
            catch (IllegalArgumentException e) {
                throw new EntityNotFoundException("Invalid game status " + e.getMessage());
            }
        }
    }

    public Iterable<Book> getAllBooks(String status) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication in /getAllBooks: " + authentication.getName());
        Optional<User> author = userRepository.findByUsername(authentication.getName());
        if (author.isEmpty())
            throw new UsernameNotFoundException("");

        if (status == null || status.isEmpty()) {
            return userBookRepository.findAllBooksByUserUsername(author.get().getUsername());
        }
        else {
            try {
                Enums.status statusEnum = Enums.status.valueOf(status);
                return userBookRepository.findBooksByStatusAndUserUsername(statusEnum, author.get().getUsername());
            }
            catch (IllegalArgumentException e) {
                throw new EntityNotFoundException("Invalid game status " + e.getMessage());
            }
        }
    }

    public Optional<UserBookDTO> get(String gbId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication in /get: " + authentication.getName());
        Optional<User> author = userRepository.findByUsername(authentication.getName());
        if (author.isEmpty())
            throw new UsernameNotFoundException("");
        Optional<UserBook> userB = userBookRepository.findByBook_GbIdAndUser_Username(gbId, author.get().getUsername());

        if (userB.isPresent()) {
            UserBook userBook = userB.get();
            UserBookDTO userBookDTO = new UserBookDTO(
                    userBook.getBook(),
                    userBook.getStatus(),
                    userBook.getUserRating(),
                    userBook.getReview()
                    //userBook.getNotes()
            );
            return Optional.of(userBookDTO);
        } else {
            return Optional.empty();
        }
    }

    public UserBook add(String gbId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication in /add: " + authentication.getName());
        Optional<User> author = userRepository.findByUsername(authentication.getName());
        if (author.isEmpty())
            throw new UsernameNotFoundException("");
        Optional<UserBook> userBook = userBookRepository.findByBook_GbIdAndUser_Username(gbId,authentication.getName());
        if (userBook.isPresent())
            throw new EntityExistsException("Book already added");
        Optional<Book> book = bookRepository.findBookByGbId(gbId);
        System.out.println(book);
        if (book.isEmpty())
            throw new IllegalArgumentException("Book not found");
        return userBookRepository.save(new UserBook(author.get(), book.get()));
    }

    @Transactional
    public void update(String gbId, UserBookUpdateDTO userBookUpdateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication in /update: " + authentication.getName());
        User author = userRepository.findByUsername(authentication.getName()).orElseThrow(
                () -> new UsernameNotFoundException(""));

        UserBook book = userBookRepository.findByBook_GbIdAndUser_Username(gbId, author.getUsername()).orElseThrow(
                () -> new EntityNotFoundException("Book with id " + gbId + " not found"));
        book.setStatus(userBookUpdateDTO.status().orElse(book.getStatus()));

        if (book.getStatus() == Enums.status.Completed) {
            achievementProcessorService.processBookCompletion(author, book);
            Optional<ReadingGoal> optionalGoal = readingGoalRepository.findByUserId(book.getUser().getId());

            optionalGoal.ifPresent(goal -> {
                goal.setCurrentBooks(goal.getCurrentBooks() + 1);
                if (goal.getCurrentBooks() >= goal.getTargetBooks()) {
                    goal.setCompleted(true);
                }
                readingGoalRepository.save(goal);
            });
        }

        book.setUserRating(userBookUpdateDTO.userRating().orElse(book.getUserRating()));
        book.setReview(userBookUpdateDTO.review().orElse(book.getReview()));

//        if (userBookUpdateDTO.notes().isPresent()) {
//            userBookUpdateDTO.notes().ifPresent(notes -> {
//                notes.forEach(note -> {
//                    note.setUser_book(book);
//                    book.getNotes().add(note);
//                });
//            });
//        }

        System.out.println("Запись изменена");
        userBookRepository.save(book);
    }

    @Transactional
    public UserBook updateBookStatus(User user, String gbId, Enums.status status) {
        UserBook userBook = userBookRepository.findByBook_GbIdAndUser_Username(gbId, user.getUsername()).orElseThrow(
                () -> new EntityNotFoundException("Book with id " + gbId + " not found"));
        userBook.setStatus(status);

        if (status == Enums.status.Completed) {
            achievementProcessorService.processBookCompletion(user, userBook);
        }

        return userBookRepository.save(userBook);
    }

    @Transactional
    public void delete(String gbId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication in /delete: " + authentication.getName());
        Optional<User> author = userRepository.findByUsername(authentication.getName());
        if (author.isEmpty())
            throw new UsernameNotFoundException("");

        userBookRepository.deleteBookByBook_GbIdAndUser_Username(gbId, author.get().getUsername());
    }

    public List<UserReviewsDTO> getBookReviews(String gbId) {
        List<UserBook> reviews = userBookRepository.findByBookGbIdAndReviewIsNotNull(gbId);
        return reviews.stream()
                .filter(review -> review.getReview() != null && !review.getReview().isEmpty())
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private UserReviewsDTO convertToDTO(UserBook userBook) {
        return new UserReviewsDTO(
                userBook.getId(),
                userBook.getUser().getUsername(),
                userBook.getReview(),
                userBook.getUserRating()
        );
    }

    public boolean isContains(String gbId) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication in /isContains: " + authentication.getName());
        Optional<User> author = userRepository.findByUsername(authentication.getName());
        if (author.isEmpty())
            throw new UsernameNotFoundException("");

        return userBookRepository.existsBookByBook_GbIdAndUser_Username(gbId, author.get().getUsername());
    }

    public UserInfo getUserInfo() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication in /getInfo: " + authentication.getName());
        Optional<User> author = userRepository.findByUsername(authentication.getName());
        if (author.isEmpty())
            throw new UsernameNotFoundException("");
        String username = author.get().getUsername();

        List <UserInfo.UserBookCountInfo> userBookQuantity = new ArrayList<>();
        for(Enums.status status: Enums.status.values())
            userBookQuantity.add(new UserInfo.UserBookCountInfo(status.name(),
                    userBookRepository.countBooksByStatusAndUser_Username(status, username)));

        List<String> genres = userBookRepository.findDistinctGenresByUsername(username);
        List<UserInfo.GenreCountInfo> genreQuantity = new ArrayList<>();
        for (String genre : genres) {
            long count = userBookRepository.countByUser_UsernameAndBook_GenresContaining(username, genre);
            genreQuantity.add(new UserInfo.GenreCountInfo(genre, count));
        }

        return new UserInfo(username,
                author.get().getSubscription().name(),
                userBookRepository.countAllByUser_Username(username),
                userBookQuantity,
                genreQuantity
                );
    }

    public Iterable<String> getAllGenre() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> author = userRepository.findByUsername(authentication.getName());
        if (author.isEmpty())
            throw new UsernameNotFoundException("");
        String username = author.get().getUsername();

        return userBookRepository.findDistinctGenresByUsername(username);
    }

    public Iterable<String> getAllAuthors() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> author = userRepository.findByUsername(authentication.getName());
        if (author.isEmpty())
            throw new UsernameNotFoundException("");
        String username = author.get().getUsername();

        return userBookRepository.findDistinctAuthorsByUsername(username).stream()
                .map(Author::getName).distinct().toList();
    }
}
