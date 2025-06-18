# Invoice Subsystem - Very Good Building & Development Company (VGB)
## Authors
- Yin Po Po Aung (yaung3@huskers.unl.edu) 
- Rometh Samarasinghe (rsamarasinghe2@huskers.unl.edu) 

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
- Custom Sorted List ADT: Generic sorted list implementation with Comparator support for maintaining invoice ordering
- Intelligent Data Ordering: Automatic maintenance of invoice ordering by total amount, customer name, and total per customer
- Enhanced API Methods: Comprehensive JDBC-based data manipulation capabilities

## System Design
The **Invoice Subsystem** is designed as an **object-oriented application** written in **Java**. It integrates:  
- Object-oriented design with appropriate inheritance relationships
- JDBC database connectivity for data persistence
- Well-defined data models representing business entities
-  Generic sorted list ADT that maintains internal ordering without external sorting
-  Flexible sorting mechanisms accepting Comparators at instantiation for different ordering criteria

## Technology Used
- Java
- JDBC (Java Database Connectivity)
- MySQL
- JUnit (for testing)
- Log4j (for logging)

## Technical Specifications
This document outlines the following key aspects required for implementing the **Invoice Subsystem**:
## Data Models
- Sorted List Integration: Custom ADT implementation for maintaining ordered invoice collections
- Automatic Ordering: Internal maintenance of invoice ordering by multiple criteria (total amount, customer name, total per customer)
## System Architecture 
Object-oriented design principles with a database-backed infrastructure  
- Generic Data Structures: Custom sorted list ADT designed to be generic and reusable
- Comparator Integration: Flexible sorting mechanisms that accept Comparators at instantiation
- No External Sorting: Internal ordering maintenance ensures no external sorting operations are required


## **Technical Implementation**
- Details on database integration, API design, and data exchange formats
## System Components
## Custom Sorted List ADT
- Generic Implementation: Designed to work with any data type
- Comparator Support: Accepts Comparator objects at instantiation for flexible sorting criteria
- Internal Ordering: Maintains ordering internally without requiring external sorting operations
## Enhanced Database Integration
- Advanced JDBC API Methods: Comprehensive set of methods for data manipulation
- Optimized Data Retrieval: Efficient querying and data access patterns
- Report Generation: Integrated with custom sorted list for high-performance report generation


---

© 2025 Very Good Building & Development Company (VGB). All rights reserved.
