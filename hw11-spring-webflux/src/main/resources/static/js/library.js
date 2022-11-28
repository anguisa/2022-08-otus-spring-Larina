window.onload = function () {

    function resetAll() {
        resetBookForm();
        resetCommentForm();
        resetCommentsTable();
    }

    // Книги

    document.getElementById('btn-get-all-books').onclick = function () {
        getAllBooks()
            .then((response) => resetAll());
    };

    document.getElementById('btn-reset-book-form').onclick = function () {
        resetBookForm();
    };

    document.getElementById('btn-save-book-form').onclick = function () {
        onBookFormSubmit();
    };

    function onBookGetComments(btn) {
        row = btn.parentElement.parentElement;
        var bookId = row.cells[0].innerHTML;
        document.getElementById('book-id-for-comments-input').value = bookId
        getAllCommentsByBookId(row.cells[0].innerHTML)
            .then((response) => resetCommentForm());
    }

    function onBookPrepareEdit(btn) {
        row = btn.parentElement.parentElement;
        document.getElementById("book-id-input").value = row.cells[0].innerHTML;
        document.getElementById("book-title-input").value = row.cells[1].innerHTML;
        document.getElementById("book-author-select").value = row.cells[2].innerHTML;
        var genres = Array.from(row.cells[4].querySelectorAll("li"), li => li.textContent);
        for (const option of document.getElementById("book-genres-select")) {
            if (genres.includes(option.value)) {
                option.selected = true;
            }
        }
    }

    function onBookDelete(btn) {
        if (confirm('Are you sure to delete this record ?')) {
            row = btn.parentElement.parentElement;
            deleteBookById(row.cells[0].innerHTML)
                .then((response) => resetAll())
                .then((response) => getAllBooks());
        }
    }

    function resetBookForm() {
        document.getElementById("book-id-input").value = "";
        document.getElementById("book-title-input").value = "";
        document.getElementById("book-author-select").value = "";
        for (const option of document.getElementById("book-genres-select")) {
            option.selected = false;
        }
    }

    function onBookFormSubmit() {
        bookTitle = document.getElementById('book-title-input').value;
        authorId = document.getElementById("book-author-select").querySelector("option:checked").value;
        genresId = Array.from(document.getElementById("book-genres-select").querySelectorAll("option:checked"), e => e.value);

        console.log(authorId);

        book = {
            title: bookTitle,
            author: {
                id: authorId
            },
            genres: []
        };

        for (var i in genresId) {
            book.genres.push({
              "id" : genresId[i]
            });
        }

        console.log(JSON.stringify(book));

        bookIdTxt = document.getElementById('book-id-input').value;

        if (!bookIdTxt) {
            createBook(book)
                .then((response) => resetAll())
                .then((response) => getAllBooks());
        } else {
            book["id"] = bookIdTxt;
            updateBook(book)
                .then((response) => resetAll())
                .then((response) => getAllBooks());
        }
    }

    async function getAllBooks() {
        return await fetch('/api/books', {
              method: 'GET',
              headers: {
                'Content-Type': 'application/json;charset=utf-8'
              }
            })
            .then((response) => {
                response.json().then(
                    books => {
                        console.log(books);
                        var new_tbody = document.createElement('tbody');
                        if (books.length > 0) {
                          books.forEach((book) => {
                            var row = new_tbody.insertRow();
                            cell0 = row.insertCell(0);
                            cell0.innerHTML = book.id;
                            cell1 = row.insertCell(1);
                            cell1.innerHTML = book.title;
                            cell2 = row.insertCell(2);
                            cell2.innerHTML = book.author.id;
                            cell3 = row.insertCell(3);
                            cell3.innerHTML = book.author.name;
                            cell4 = row.insertCell(4);
                            var temp = "<ul>";
                            book.genres.forEach((genre) => {
                                temp += "<li>" + genre.id + "</li>";
                            });
                            temp += "</ul>";
                            cell4.innerHTML = temp;
                            cell5 = row.insertCell(5);
                            temp = "<ul>";
                            book.genres.forEach((genre) => {
                                temp += "<li>" + genre.title + "</li>";
                            });
                            temp += "</ul>";
                            cell5.innerHTML = temp;
                            cell6 = row.insertCell(6);
                            cell6.innerHTML = `<button type="btn-edit-book">Edit</button>`;
                            cell7 = row.insertCell(7);
                            cell7.innerHTML = `<button type="btn-delete-book">Delete</button>`;
                            cell8 = row.insertCell(8);
                            cell8.innerHTML = `<button type="btn-get-comments">Comments</button>`;
                          });
                        }
                        var old_tbody = document.getElementById('tab-books-data').getElementsByTagName('tbody')[0];
                        old_tbody.parentNode.replaceChild(new_tbody, old_tbody);
                        document.querySelectorAll('[type=btn-edit-book]').forEach((btn) => {
                            btn.addEventListener('click', function() {
                              onBookPrepareEdit(this);
                            });
                        });
                        document.querySelectorAll('[type=btn-delete-book]').forEach((btn) => {
                            btn.addEventListener('click', function() {
                              onBookDelete(this);
                            });
                        });
                        document.querySelectorAll('[type=btn-get-comments]').forEach((btn) => {
                            btn.addEventListener('click', function() {
                              onBookGetComments(this);
                            });
                        });
                    }
                )
            });
    }

    async function deleteBookById(id) {
        return await fetch('/api/books/' + id, {
            method: 'DELETE',
            headers: {
              'Content-Type': 'application/json;charset=utf-8'
            }
        });
    }

    async function updateBook(book) {
        return await fetch('/api/books/' + book.id, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json;charset=utf-8'
          },
          body: JSON.stringify(book)
        });
    }

    async function createBook(book) {
        return await fetch('/api/books', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json;charset=utf-8'
          },
          body: JSON.stringify(book)
        });
    }

    // Комментарии

    document.getElementById('btn-reset-comment-form').onclick = function () {
        resetCommentForm();
    };

    document.getElementById('btn-save-comment-form').onclick = function () {
        onCommentFormSubmit();
    };

    function onCommentPrepareEdit(btn) {
        row = btn.parentElement.parentElement;
        document.getElementById("comment-id-input").value = row.cells[0].innerHTML;
        document.getElementById("comment-text-input").value = row.cells[1].innerHTML;
    }

    function onCommentDelete(btn) {
        if (confirm('Are you sure to delete this record ?')) {
            row = btn.parentElement.parentElement;
            commentId = row.cells[0].innerHTML;
            bookId = document.getElementById('book-id-for-comments-input').value;
            deleteCommentById(bookId, commentId)
                .then((response) => resetCommentForm())
                .then((response) => getAllCommentsByBookId(bookId));
        }
    }

    function resetCommentForm() {
        document.getElementById("comment-id-input").value = "";
        document.getElementById("comment-text-input").value = "";
    }

    function resetCommentsTable() {
        var old_tbody = document.getElementById('tab-comments-data').getElementsByTagName('tbody')[0];
        old_tbody.parentNode.replaceChild(document.createElement('tbody'), old_tbody);
        document.getElementById('book-id-for-comments-input').value = ""
    }

    function onCommentFormSubmit() {
        commentText = document.getElementById('comment-text-input').value;
        bookId = document.getElementById('book-id-for-comments-input').value;

        comment = {
            text: commentText,
            book: {
                id: bookId
            }
        };

        console.log(JSON.stringify(comment));

        commentIdTxt = document.getElementById('comment-id-input').value;

        if (!commentIdTxt) {
            createComment(bookId, comment)
                .then((response) => resetCommentForm())
                .then((response) => getAllCommentsByBookId(bookId));
        } else {
            comment["id"] = commentIdTxt;
            updateComment(bookId, comment)
                .then((response) => resetCommentForm())
                .then((response) => getAllCommentsByBookId(bookId));
        }
    }

    async function getAllCommentsByBookId(bookId) {
        return await fetch('/api/books/' + bookId + "/comments", {
              method: 'GET',
              headers: {
                'Content-Type': 'application/json;charset=utf-8'
              }
            })
            .then((response) => {
                response.json().then(
                    comments => {
                        console.log(comments);
                        var new_tbody = document.createElement('tbody');
                        if (comments.length > 0) {
                          comments.forEach((comment) => {
                            var row = new_tbody.insertRow();
                            cell0 = row.insertCell(0);
                            cell0.innerHTML = comment.id;
                            cell1 = row.insertCell(1);
                            cell1.innerHTML = comment.text;
                            cell2 = row.insertCell(2);
                            cell2.innerHTML = `<button type="btn-edit-comment">Edit</button>`;
                            cell3 = row.insertCell(3);
                            cell3.innerHTML = `<button type="btn-delete-comment">Delete</button>`;
                          });
                        }
                        var old_tbody = document.getElementById('tab-comments-data').getElementsByTagName('tbody')[0];
                        old_tbody.parentNode.replaceChild(new_tbody, old_tbody);
                        document.querySelectorAll('[type=btn-edit-comment]').forEach((btn) => {
                            btn.addEventListener('click', function() {
                              onCommentPrepareEdit(this);
                            });
                        });
                        document.querySelectorAll('[type=btn-delete-comment]').forEach((btn) => {
                            btn.addEventListener('click', function() {
                              onCommentDelete(this);
                            });
                        });
                    }
                )
            });
    }

    async function deleteCommentById(bookId, commentId) {
        return await fetch('/api/books/' + bookId + "/comments/" + commentId, {
            method: 'DELETE',
            headers: {
              'Content-Type': 'application/json;charset=utf-8'
            }
        });
    }

    async function updateComment(bookId, comment) {
        return await fetch('/api/books/' + bookId + "/comments/" + comment.id, {
          method: 'PUT',
          headers: {
            'Content-Type': 'application/json;charset=utf-8'
          },
          body: JSON.stringify(comment)
        });
    }

    async function createComment(bookId, comment) {
        return await fetch('/api/books/' + bookId + "/comments", {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json;charset=utf-8'
          },
          body: JSON.stringify(comment)
        });
    }
}