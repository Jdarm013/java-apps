# Java Applications

A collection of four standalone Java applications built while learning object-oriented design, data structures, and JavaFX GUI development. Each project was built around a real-world domain — contacts, tasks, flights, and e-commerce — to make the underlying concepts concrete rather than abstract.

---

## Projects

### 1. Contacts App — `ContactsAppStarter/`
A JavaFX phonebook application backed by a hand-rolled generic doubly linked list.

**What it does:** Add, edit, delete, and search contacts through a desktop GUI. Contacts persist to a CSV file between sessions and are reloaded on startup. Search works in real time as you type, matching against both name and phone number digits. A toggle switches the traversal direction of the underlying list, demonstrating forward and backward iteration live in the UI.

**What's interesting about it technically:**
- The `DoublyLinkedList<E>` is implemented from scratch with a generic type parameter, handling four distinct removal cases (lone node, head, tail, middle) and exposing both a forward `Iterator` and a backward `Iterable` via inner classes.
- Search results display an iteration counter showing how many nodes were traversed — a small window into what's actually happening in the data structure.
- Phone number input auto-formats 10-digit strings to `XXX-XXX-XXXX` on add.
- Status messages use `PauseTransition` for timed color-coded feedback without blocking the UI thread.
- On exit, the contact list is saved back to CSV sorted alphabetically by name.

**Tech:** Java 17, JavaFX, Maven, CSV persistence

---

### 2. Task Manager App — `TaskManagerAppStarter/`
A JavaFX task management application using a `PriorityQueue` as its core data structure.

**What it does:** Create, edit, delete, and track tasks through a sortable table UI. Tasks have a subject, priority level (URGENT / HIGH / NORMAL / LOW), status (NOT STARTED / IN PROGRESS / COMPLETED), start date, and due date. A progress bar tracks overall completion. A "Peek" button reveals the single highest-priority task without removing it from the queue. All data persists to CSV.

**What's interesting about it technically:**
- `Task` implements `Comparable<Task>` with a three-level sort: completed tasks always sink to the bottom, active tasks sort by priority enum order, and ties break by due date.
- The `PriorityQueue` is the live source of truth. Edits work by removing the old task object and re-inserting the updated one so the queue re-sorts correctly.
- The UI uses a `FilteredList` wrapping a `SortedList` wrapping an `ObservableList` — a JavaFX pipeline that keeps search and hide-completed filters composable without duplicating data.
- Table cells use custom `CellFactory` implementations to apply color-coded styles per priority and status.
- ID generation uses a stream over the queue: `taskQueue.stream().mapToInt(Task::getTaskId).max().orElse(0) + 1`

**Tech:** Java 17, JavaFX, Maven, CSV persistence

---

### 3. Flight Operations Simulator — `FlightOperationsStub-2/`
A console application simulating an airport operations board using a `LinkedList` and `Stack`.

**What it does:** Loads a flight schedule from CSV, randomly reassigns operation statuses, removes cancelled flights, inserts VIP flights at the head of the queue, and moves queued flights to the back — printing the board state after each operation.

**What's interesting about it technically:**
- Cancelled flight removal uses a `Stack` as a staging buffer to collect flights for deletion before removing them. Removing from a `LinkedList` while iterating over it causes a `ConcurrentModificationException` — the stack pattern is a deliberate workaround for that.
- `Flight` implements a generic `CSVTemplate` interface, enabling a reusable `CSVReaderWriter<T>` to load any conforming type from a file using reflection.
- The VIP insertion method (`presidentAndCroniesJumpTheQueue`) uses `addFirst()` to demonstrate head insertion on a linked list.

**Tech:** Java 17, Maven, CSV data (flights and employees)

---

### 4. Amazon Order System — `AmazonOrderProject/`
A multi-class OOP console simulation of an e-commerce order lifecycle.

**What it does:** An interactive console session walks a customer through browsing a product catalog, adding items to a cart, and checking out. The simulation then steps through the full order lifecycle — placement, warehouse processing, shipment, carrier delay, and final delivery — printing status notifications, an SMS alert, and a formatted invoice at each stage.

**What's interesting about it technically:**
- The domain model spans nine classes: `Customer`, `Address`, `Product`, `OrderItem`, `Order`, `Shipment`, `Payment`, `ReceiptService`, and `AmazonOrdersMain`. Each class has a single responsibility.
- `Order` uses an `OrderStatus` enum with values `PENDING → PROCESSING → SHIPPED → DELIVERED`, and state transitions are explicit method calls that mirror how a real order system would work.
- `Address` includes a `hasSameAddress()` comparison so the receipt prints a single address block when billing and shipping match, and two separate blocks when they differ.
- Input validation loops on both customer lookup and quantity entry, handling `NumberFormatException` with a retry rather than a crash.

**Tech:** Java 17, Maven (no external dependencies)

---

## Shared Utility: `CSVReaderWriter<T>`

Three of these four projects share a generic CSV utility class (`CSVReaderWriter<T>`) that can load and save any object implementing the `CSVTemplate` interface. `CSVTemplate` defines two methods: `toCSV()` serializes the object to a comma-separated string, and `fromCSV(String[] parts)` populates a blank object from a parsed row. The reader uses reflection to instantiate new objects without knowing their type at compile time. This pattern came out of noticing that the contacts, tasks, and flights projects all needed the same file I/O logic and factoring it out.
