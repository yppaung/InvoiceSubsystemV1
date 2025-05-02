# Invoice Subsystem - Very Good Building & Development Company (VGB)

## Overview
This document provides the technical design for the **Invoice Subsystem** of **Very Good Building & Development Company (VGB)**, Ron Swanson's company. VGB is a construction company involved in various aspects of the industry, including:  
- General contracting  
- Subcontracting  
- Sales  
- Leasing and rental of construction equipment and materials  

The system modernizes VGB's invoice management process by replacing spreadsheets and physical records with a robust database-backed solution. Built as an object-oriented Java application, it handles diverse transaction types with specific tax rules and calculation models.

## Key Features
- Multi-transaction support: Equipment purchases/leases/rentals, Material purchases, Contract services
- Calculations: Specialized pricing models and tax calculations based on transaction type
- Database integration: MySQL backend with JDBC connection management
- Report generation: Summary reports, customer-specific reports, detailed invoice reports

## System Design
The **Invoice Subsystem** is designed as an **object-oriented application** written in **Java**. It integrates:  
- Object-oriented design with appropriate inheritance relationships
- JDBC database connectivity for data persistence
- Well-defined data models representing business entities

## Technology Used
- Java
- JDBC (Java Database Connectivity)
- MySQL
- JUnit (for testing)
- Log4j (for logging)

## Technical Specifications
This document outlines the following key aspects required for implementing the **Invoice Subsystem**:
- **Data Models**: Defining structured representations of invoices and related entities  
- **System Architecture**: Object-oriented design principles with a database-backed infrastructure  
- **Technical Implementation**: Details on database integration, API design, and data exchange formats  

## Future Enhancements
As the system evolves, future improvements may include:
- Integrate a custom sorted list ADT to replace existing data structures.
- Maintain invoice ordering by total amount, customer name, and total per customer.
- Ensure ordering is maintained internally—no external sorting allowed.
- Design the list to be generic and accept a Comparator at instantiation.
- Update the application to use the new list ADT for report generation.
- Improve API methods in for data manipulation via JDBC.

---

© 2025 Very Good Building & Development Company (VGB). All rights reserved.
