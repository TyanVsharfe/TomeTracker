package com.tometracker.service;

import com.tometracker.data_template.Enums;
import com.tometracker.db.model.Book;
import com.tometracker.db.model.User;
import com.tometracker.db.model.UserBook;
import com.tometracker.db.repository.BookRepository;
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

    public UserBookService(UserBookRepository userBookRepository, BookRepository bookRepository, UserRepository userRepository) {
        this.userBookRepository = userBookRepository;
        this.bookRepository = bookRepository;
        this.userRepository = userRepository;
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

    public void update(String gbId, UserBookUpdateDTO userBookUpdateDTO) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Authentication in /update: " + authentication.getName());
        Optional<User> author = userRepository.findByUsername(authentication.getName());
        if (author.isEmpty())
            throw new UsernameNotFoundException("");

        UserBook book = userBookRepository.findByBook_GbIdAndUser_Username(gbId, author.get().getUsername()).orElseThrow(
                () -> new EntityNotFoundException("Book with id " + gbId + " not found"));
        book.setStatus(userBookUpdateDTO.status().orElse(book.getStatus()));
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
            userBookQuantity.add(new UserInfo.UserBookCountInfo(status.name(), userBookRepository.countBooksByStatusAndUser_Username(status, username)));

        return new UserInfo(username,
                author.get().getSubscription().name(),
                userBookRepository.countAllByUser_Username(username),
                userBookQuantity
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
}
