package ru.shishmakov.service;

import lombok.RequiredArgsConstructor;
import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.shishmakov.domain.Author;
import ru.shishmakov.domain.Book;
import ru.shishmakov.domain.Comment;
import ru.shishmakov.domain.Genre;
import ru.shishmakov.repository.AuthorRepository;
import ru.shishmakov.repository.BookRepository;
import ru.shishmakov.repository.GenreRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static java.lang.System.lineSeparator;
import static java.util.stream.Collectors.joining;

@RequiredArgsConstructor
@Service
@Transactional(readOnly = true)
public class LibraryService {
    private final GenreRepository genreRepository;
    private final BookRepository bookRepository;
    private final AuthorRepository authorRepository;

    public String getAllBooks() {
        return bookRepository.findAll().stream().map(Book::toString).collect(joining(lineSeparator()));
    }

    public String getAllAuthors() {
        return authorRepository.findAll().stream().map(Author::toString).collect(joining(lineSeparator()));
    }

    public String getAllGenres() {
        return genreRepository.findAll().stream().map(Genre::toString).collect(joining(lineSeparator()));
    }

    public String getBookAuthors(ObjectId bookId) {
        Optional<Book> book = bookRepository.findByIdWithFetchAuthors(bookId);
        return book.map(b -> new StringBuilder("Book:")
                .append(lineSeparator())
                .append(b.toString())
                .append(lineSeparator())
                .append("Authors:")
                .append(lineSeparator())
                .append(b.getAuthors().stream().map(Author::toString).collect(joining(lineSeparator())))
                .toString())
                .orElseGet(() -> "book: " + bookId + " not found");

    }

    public String getBookGenres(ObjectId bookId) {
        Optional<Book> book = bookRepository.findByIdWithFetchGenres(bookId);
        return book.map(b -> new StringBuilder("Book:")
                .append(lineSeparator())
                .append(b.toString())
                .append(lineSeparator())
                .append("Genres:")
                .append(lineSeparator())
                .append(b.getGenres().stream().map(Genre::toString).collect(joining(lineSeparator())))
                .toString())
                .orElseGet(() -> "book: " + bookId + " not found");
    }

    public String getBookComments(ObjectId bookId) {
        Optional<Book> book = bookRepository.findByIdWithFetchComments(bookId);
        return book.map(b -> new StringBuilder("Book:")
                .append(lineSeparator())
                .append(b.toString())
                .append(lineSeparator())
                .append("Comments:")
                .append(lineSeparator())
                .append(b.getComments().stream().map(Comment::toString).collect(joining(lineSeparator())))
                .toString())
                .orElseGet(() -> "book: " + bookId + " not found");
    }

    public String createBook(String title, String isbn, Set<ObjectId> authorIds, Set<ObjectId> genreIds) {
        Book book = Book.builder().title(title).isbn(isbn).build();

        List<Author> authors = authorRepository.findAllByIdFetchId(authorIds);
        List<Genre> genres = genreRepository.findAllByIdFetchId(genreIds);

        book.addAuthors(authors);
        book.addGenres(genres);
        bookRepository.save(book);
        return book.toString();
    }

    public String createBookComment(ObjectId bookId, String commentText) {
        return bookRepository.findById(bookId)
                .map(b -> {
                    System.out.println(b);
                    Comment comment = Comment.builder().text(commentText).createDate(Instant.now()).build();
                    b.addComment(comment);
                    bookRepository.save(b);
                    return comment.toString();
                })
                .orElseGet(() -> "book: " + bookId + " not found");
    }

//    public void deleteBook(long bookId) {
//        bookRepository.findByIdWithFetchCommentsGenresAuthors(bookId).ifPresent(b -> {
//            b.removeAllAuthors();
//            b.removeAllGenres();
//            b.removeAllComment();
//            bookRepository.delete(b);
//        });
//    }
//
//    public void deleteComment(long commentId) {
//        commentRepository.findByIdWithFetchBook(commentId).ifPresent(comment -> {
//            comment.getBook().removeComment(comment);
//            commentRepository.delete(comment);
//        });
//    }

    public void exit() {
        System.out.println(lineSeparator() + "\tGoodbye! =)" + lineSeparator());
        System.exit(0);
    }
}
