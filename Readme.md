# fare-calculator

A java based application that reads passenger tap on and tap off data from a csv file and generate the summary of passenger trips with fare calculation.


## Features

- Parses taps (ON and OFF) from `taps.csv`
- Matches taps into trips (COMPLETED, CANCELLED, INCOMPLETE)
- Calculates fare based on predefined fare rules
- Outputs a `trips.csv` file with trip summaries

## Tech Stack

- Java 21
- Spring Boot 3
- Maven
- Lombok
- OpenCSV
- JUnit 5

## Assumptions
- Input file is well-formed
- Each PAN on a Bus can only have one active `ON` tap at a time
- Consecutive `ON` taps without an `OFF` are treated as multiple incomplete trips
- An `OFF` without a matching `ON` is ignored (print warning)
- Taps with the same stop ID (`ON` then `OFF`) are treated as CANCELLED trips

## Running the App
Note:
OpenCSV’s @CsvBindByName annotation does not preserve column order when writing CSV files — a known limitation of the library.

To ensure a consistent column order in the output (e.g., trips.csv), a custom column mapping strategy was applied. However, this approach disables field-level custom converters (e.g., BigDecimalConverter) during write operations.

While there are workarounds available, such as manually mapping and formatting fields during CSV writing — these were not implemented due to time constraints during the coding exercise.

1. Clone the Project:
git clone https://github.com/umairansariz/littlepay-exercise.git

2. Navigate to the Project Directory:
cd littlepay-exercise\fare-calculator

3. Place the Input File:
Ensure the taps.csv file is placed in the root directory of the project (the same directory where pom.xml is located).

4. Build the Application:
```bash
mvn clean install
```
This command will compile the Java code, run the unit tests, and package the application into an executable JAR file in the target directory.

5. Run the Application:
```bash
java -jar target/fare-calculator-1.0-SNAPSHOT.jar
```

6. Check the `trips.csv` file for results
