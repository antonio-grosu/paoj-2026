package com.pao.laboratory12;

import com.pao.laboratory12.model.Author;
import com.pao.laboratory12.model.Book;
import com.pao.laboratory12.model.Reader;
import com.pao.laboratory12.repository.AuthorRepository;
import com.pao.laboratory12.repository.BookRepository;
import com.pao.laboratory12.repository.LoanRepository;
import com.pao.laboratory12.repository.ReaderRepository;
import com.pao.laboratory12.service.AuditService;
import com.pao.laboratory12.service.LibraryService;
import com.pao.laboratory12.util.DatabaseConnection;
import com.pao.laboratory12.util.SchemaInitializer;

import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        SchemaInitializer.init(DatabaseConnection.getInstance().getConnection());

        AuditService audit = AuditService.getInstance();
        AuthorRepository authorRepo = new AuthorRepository();
        BookRepository bookRepo = new BookRepository();
        ReaderRepository readerRepo = new ReaderRepository();
        LoanRepository loanRepo = new LoanRepository();
        LibraryService libraryService = LibraryService.getInstance();

        System.out.println("=== BIBLIOTECA JDBC — Demo Lab12 ===\n");

        Author author = new Author("Gabriel Garcia Marquez", "CO");
        authorRepo.save(author);
        audit.log("add_author");
        System.out.println("1. Autor adaugat: " + author);

        Book book1 = new Book("100 de ani de singuratate", author.getId());
        Book book2 = new Book("Dragostea in vremea holerei", author.getId());
        bookRepo.save(book1);
        bookRepo.save(book2);
        audit.log("add_book");
        System.out.println("2. Carti adaugate: " + book1 + ", " + book2);

        Reader reader = new Reader("Ion Popescu", "ion.popescu@email.com");
        readerRepo.save(reader);
        audit.log("add_reader");
        System.out.println("3. Cititor adaugat: " + reader);

        List<Book> allBooks = bookRepo.findAll();
        audit.log("list_books");
        System.out.println("4. Toate cartile (" + allBooks.size() + "):");
        allBooks.forEach(b -> System.out.println("   " + b));

        bookRepo.findById(book1.getId()).ifPresentOrElse(
                b -> System.out.println("5. Carte gasita: " + b),
                () -> System.out.println("5. Carte negasita.")
        );
        audit.log("find_book_by_id");

        book1.setTitle("100 de ani de singuratate (Ed. speciala)");
        bookRepo.update(book1);
        audit.log("update_book");
        System.out.println("6. Carte actualizata: " + book1);

        long loanId = libraryService.borrowBook(reader.getId(), book1.getId());
        audit.log("borrow_book");
        System.out.println("7. Imprumut creat cu ID=" + loanId);

        libraryService.returnBook(loanId);
        audit.log("return_book");
        System.out.println("8. Carte returnata.");

        List<String> activeLoans = libraryService.getActiveLoansWithDetails();
        audit.log("report_active_loans");
        System.out.println("9. Imprumuturi active: " + (activeLoans.isEmpty() ? "niciun" : ""));
        activeLoans.forEach(s -> System.out.println("   " + s));

        System.out.println("   Top carti:");
        libraryService.getTopBorrowedBooksWithAuthor().forEach(s -> System.out.println("   " + s));

        System.out.println("   Imprumuturi per cititor:");
        libraryService.getLoansCountPerReader().forEach(s -> System.out.println("   " + s));

        loanRepo.delete(loanId);
        readerRepo.delete(reader.getId());
        audit.log("delete_reader");
        System.out.println("10. Cititor sters cu ID=" + reader.getId());

        System.out.println("\n=== Demo finalizat. Verifica audit.csv ===");
        DatabaseConnection.getInstance().close();
    }
}
