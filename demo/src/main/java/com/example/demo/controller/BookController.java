package com.example.demo.controller;

import com.example.demo.model.Book;
import com.example.demo.service.BookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/books")
public class BookController {

    private final BookService bookService;

    @Autowired
    public BookController(BookService bookService) {
        this.bookService = bookService;
    }

    // Show form to create a new book
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("book", new Book());
        return "book-create"; // Thymeleaf template name
    }

    // Process the form to create a new book
    @PostMapping("/create")
    public String createBook(@ModelAttribute Book book, Model model) {
        try {
            bookService.createBook(book);
            return "redirect:/books/list";
        } catch (Exception e) {
            // Log the exception if necessary
            model.addAttribute("errorMessage", "Error creating book: " + e.getMessage());
            model.addAttribute("book", book); // Keep user input in the form
            return "book-create";
        }
    }

    // View details of a specific book
    @GetMapping("/{id}")
    public String findBookById(@PathVariable Long id, Model model) {
        Optional<Book> bookOptional = bookService.findBookById(id);
        if (bookOptional.isPresent()) {
            model.addAttribute("book", bookOptional.get());
            return "book-detail"; // Thymeleaf template name
        } else {
            model.addAttribute("errorMessage", "Book not found with ID: " + id);
            return "error/404"; // Thymeleaf error template
        }
    }

    // List all books
    @GetMapping("/list")
    public String findAllBooks(Model model) {
        List<Book> books = bookService.findAllBooks();
        model.addAttribute("books", books);
        return "book-list"; // Thymeleaf template name
    }

    // Show form to edit an existing book
    @GetMapping("/edit/{id}")
    public String showUpdateForm(@PathVariable Long id, Model model) {
        Optional<Book> bookOptional = bookService.findBookById(id);
        if (bookOptional.isPresent()) {
            model.addAttribute("book", bookOptional.get());
            return "book-edit"; // Thymeleaf template name
        } else {
            model.addAttribute("errorMessage", "Book not found with ID: " + id + " for editing.");
            return "error/404"; // Thymeleaf error template
        }
    }

    // Process the form to update an existing book
    @PostMapping("/edit/{id}")
    public String updateBook(@PathVariable Long id, @ModelAttribute Book book, Model model) {
        // The book object from @ModelAttribute might not have the ID set if the form doesn't include it.
        // It's good practice to set it from the path variable to ensure consistency.
        // However, bookService.updateBook(id, book) should handle fetching by 'id'
        // and then updating fields from 'book'.
        // If 'book' from the form has an ID, it should ideally match 'id'.
        // For this implementation, we assume bookService.updateBook correctly uses the 'id' path variable.
        try {
            book.setId(id); // Ensure the ID is set for the update operation
            bookService.updateBook(id, book);
            return "redirect:/books/list";
        } catch (RuntimeException e) {
            model.addAttribute("errorMessage", "Error updating book: " + e.getMessage());
            model.addAttribute("book", book); // Keep user input in the form
            return "book-edit"; // Return to edit form with error
        }
    }

    // Delete a book
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, Model model) {
        try {
            bookService.deleteBook(id);
            return "redirect:/books/list";
        } catch (Exception e) {
            // This might happen if the book doesn't exist, or due to other constraints
            // Log the exception
            model.addAttribute("errorMessage", "Error deleting book with ID: " + id + ". " + e.getMessage());
            // Optionally, redirect to an error page or back to the list with a message
            List<Book> books = bookService.findAllBooks();
            model.addAttribute("books", books);
            return "book-list"; // Or a specific error view
        }
    }
}
