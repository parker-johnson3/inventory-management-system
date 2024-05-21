-- need to change
USE appdb;

CREATE TABLE Customer (
    CustomerId INT NOT NULL AUTO_INCREMENT,
    `Name` VARCHAR(100),
    `Description` VARCHAR(255),
    PRIMARY KEY (CustomerId)
);

CREATE TABLE Airplane (
    AirplaneId INT NOT NULL AUTO_INCREMENT,
    `Name` VARCHAR(100),
    Description VARCHAR(255),
    City VARCHAR(100),
    State VARCHAR(100),
    ProductionStage VARCHAR(100),
    Cost DECIMAL,
    DateStarted DATE,
    DateFinished DATE,
    FacilityId INT,
    SeatingCapacity INT,
    `Size` VARCHAR(100),
    HasFirstClass BOOLEAN,
    PRIMARY KEY(AirplaneId)
);

CREATE TABLE Supplier(
    SupplierId INT NOT NULL AUTO_INCREMENT,
    `Name` VARCHAR(100),
    `Description` VARCHAR(255),
    ComponentTypesList VARCHAR(255),        -- Comma Delimited List of Component Types this supplier makes
    PRIMARY KEY(SupplierId)
);

CREATE TABLE Component(
    ComponentId INT NOT NULL AUTO_INCREMENT,
    `Name` VARCHAR(100),
    `Description` VARCHAR(255),
    City VARCHAR(100),
    State VARCHAR(100),
    ComponentType VARCHAR(100),
    FacilityId INT DEFAULT 0,           -- 0 = Made In House
    Cost DECIMAL,
    ProductionStage VARCHAR(100),
    PRIMARY KEY(ComponentId)
);

CREATE TABLE Facility(
    FacilityId INT NOT NULL AUTO_INCREMENT,
    `Name` VARCHAR(100),
    City VARCHAR(100),
    State VARCHAR(100),
    `Description` VARCHAR(255),
    ComponentsInProduction INT,
    ComponentsCompleted INT,
    ModelsInProduction INT,
    ModelsCompleted INT,
    EmployeeCount INT,
    ManagerId INT,
    PRIMARY KEY(FacilityId)
);

CREATE TABLE Manager(
    ManagerId INT NOT NULL AUTO_INCREMENT,
    `Name` VARCHAR(100),
    `Password` VARCHAR(255),
    Position VARCHAR(100),
    AccessLevel INT,
    FacilityId INT,
    PRIMARY KEY(ManagerId)
);

CREATE TABLE SupplierFacility (
    Id INT NOT NULL AUTO_INCREMENT,
    SupplierId INT,
    FacilityId INT,
    PRIMARY KEY(Id)
);

CREATE TABLE AirplaneComponent (
    Id INT NOT NULL AUTO_INCREMENT,
    AirplaneId INT,
    ComponentId INT,
    PRIMARY KEY(Id)
);

-- Setup IDs
ALTER TABLE Customer AUTO_INCREMENT=3001;
ALTER TABLE Airplane AUTO_INCREMENT=1001;
ALTER TABLE Facility AUTO_INCREMENT=6001;
ALTER TABLE Manager AUTO_INCREMENT=8001;


-- AIRPLANE
INSERT INTO Airplane (`Name`, Description, City, State, ProductionStage, Cost, DateStarted, DateFinished, FacilityId, SeatingCapacity, `Size`, HasFirstClass)
VALUES ('Boeing 747', 'A large, long-range wide-body airliner with a capacity of 350 passengers, known for its iconic hump upper deck design.', 'Seattle', 'Washington', 'Unstarted', 250000000.00, '2023-01-01', '2024-01-01', 6001, 350, 'Large', true);

INSERT INTO Airplane (`Name`, Description, City, State, ProductionStage, Cost, DateStarted, DateFinished, FacilityId, SeatingCapacity, `Size`, HasFirstClass)
VALUES ('Airbus A380', 'An extra-large, double-deck, wide-body airliner with a capacity of 500 passengers, designed for long-haul flights.', 'Seattle', 'Washington', 'Finished', 275000000.00, '2023-02-01', '2024-02-01', 6001, 500, 'Extra-Large', true);

INSERT INTO Airplane (`Name`, Description, City, State, ProductionStage, Cost, DateStarted, DateFinished, FacilityId, SeatingCapacity, `Size`, HasFirstClass)
VALUES ('Cessna 172', 'A small, single-engine aircraft primarily used for flight training and personal travel.', 'Dallas', 'Texas', 'Unstarted', 80000.00, '2023-03-01', '2023-06-01', 6002, 75, 'Small', false);

INSERT INTO Airplane (`Name`, Description, City, State, ProductionStage, Cost, DateStarted, DateFinished, FacilityId, SeatingCapacity, `Size`, HasFirstClass)
VALUES ('Bombardier Global 7500', 'A medium-sized, long-range business jet with a seating capacity of 225 passengers, known for its luxurious cabin.', 'Dallas', 'Texas', 'In-Progress', 73000000.00, '2023-04-01', '2023-09-01', 6002, 225, 'Medium', true);

INSERT INTO Airplane (`Name`, Description, City, State, ProductionStage, Cost, DateStarted, DateFinished, FacilityId, SeatingCapacity, `Size`, HasFirstClass)
VALUES ('Embraer Phenom 300', 'A medium-sized business jet with a seating capacity of 225 passengers, known for its performance and efficiency.', 'Miami', 'Florida', 'Finished', 9200000.00, '2023-05-01', '2023-10-01', 6003, 225, 'Medium', false);

INSERT INTO Airplane (`Name`, Description, City, State, ProductionStage, Cost, DateStarted, DateFinished, FacilityId, SeatingCapacity, `Size`, HasFirstClass)
VALUES ('Gulfstream G650', 'A large, long-range business jet with a seating capacity of 350 passengers, known for its speed and range.', 'Miami', 'Florida', 'Unstarted', 70000000.00, '2023-06-01', '2024-01-01', 6003, 350, 'Large', true);

INSERT INTO Airplane (`Name`, Description, City, State, ProductionStage, Cost, DateStarted, DateFinished, FacilityId, SeatingCapacity, `Size`, HasFirstClass)
VALUES ('Dassault Falcon 7X', 'A large, long-range business jet with a seating capacity of 350 passengers, known for its advanced technology and comfort.', 'Chicago', 'Illinois', 'Finished', 55000000.00, '2023-07-01', '2023-12-01', 6004, 350, 'Large', true);

INSERT INTO Airplane (`Name`, Description, City, State, ProductionStage, Cost, DateStarted, DateFinished, FacilityId, SeatingCapacity, `Size`, HasFirstClass)
VALUES ('Boeing 787', 'A large, long-range wide-body airliner with a seating capacity of 350 passengers, known for its fuel efficiency and advanced features.', 'Chicago', 'Illinois', 'Unstarted', 200000000.00, '2023-08-01', '2024-04-01', 6004, 350, 'Large', true);

INSERT INTO Airplane (`Name`, Description, City, State, ProductionStage, Cost, DateStarted, DateFinished, FacilityId, SeatingCapacity, `Size`, HasFirstClass)
VALUES ('Airbus A320', 'A medium-sized, single-aisle airliner with a seating capacity of 225 passengers, known for its reliability and efficiency.', 'Los Angeles', 'California', 'Finished', 110000000.00, '2023-09-01', '2024-05-01', 6005, 225, 'Medium', true);

INSERT INTO Airplane (`Name`, Description, City, State, ProductionStage, Cost, DateStarted, DateFinished, FacilityId, SeatingCapacity, `Size`, HasFirstClass)
VALUES ('Cirrus SR22', 'A small, single-engine aircraft primarily used for personal travel and flight training, with a seating capacity of 75.', 'Houston', 'Texas', 'Unstarted', 650000.00, '2023-10-01', '2023-11-01', 6006, 75, 'Small', false);

INSERT INTO Airplane (`Name`, Description, City, State, ProductionStage, Cost, DateStarted, DateFinished, FacilityId, SeatingCapacity, `Size`, HasFirstClass)
VALUES ('Pilatus PC-12', 'A medium-sized, single-engine aircraft used for executive transport and regional travel, with a seating capacity of 225 passengers.', 'New York', 'New York', 'In-Progress', 4500000.00, '2023-11-01', '2024-02-01', 6007, 225, 'Medium', false);

INSERT INTO Airplane (`Name`, Description, City, State, ProductionStage, Cost, DateStarted, DateFinished, FacilityId, SeatingCapacity, `Size`, HasFirstClass)
VALUES ('Cessna Citation Longitude', 'A large, long-range business jet with a seating capacity of 350 passengers, known for its comfort and performance.', 'Atlanta', 'Georgia', 'Finished', 27000000.00, '2023-12-01', '2024-03-01', 6008, 350, 'Large', true);

INSERT INTO Airplane (`Name`, Description, City, State, ProductionStage, Cost, DateStarted, DateFinished, FacilityId, SeatingCapacity, `Size`, HasFirstClass)
VALUES ('Embraer Legacy 500', 'A medium-sized business jet with a seating capacity of 225 passengers, known for its range and spacious cabin.', 'Atlanta', 'Georgia', 'Unstarted', 21000000.00, '2024-01-01', '2024-06-01', 6008, 225, 'Medium', true);

INSERT INTO Airplane (`Name`, Description, City, State, ProductionStage, Cost, DateStarted, DateFinished, FacilityId, SeatingCapacity, `Size`, HasFirstClass)
VALUES ('Bombardier Challenger 350', 'A medium-sized business jet with a seating capacity of 225 passengers, known for its performance and comfort.', 'Chicago', 'Illinois', 'Finished', 25000000.00, '2024-02-01', '2024-07-01', 6004, 225, 'Medium', true);

INSERT INTO Airplane (`Name`, Description, City, State, ProductionStage, Cost, DateStarted, DateFinished, FacilityId, SeatingCapacity, `Size`, HasFirstClass)
VALUES ('Gulfstream G550', 'A large, long-range business jet with a seating capacity of 350 passengers, known for its range and cabin comfort.', 'Seattle', 'Washington', 'Unstarted', 60000000.00, '2024-03-01', '2024-08-01', 6001, 350, 'Large', true);

INSERT INTO Airplane (`Name`, Description, City, State, ProductionStage, Cost, DateStarted, DateFinished, FacilityId, SeatingCapacity, `Size`, HasFirstClass)
VALUES ('Dassault Falcon 2000S', 'A large, long-range business jet with a seating capacity of 350 passengers, known for its efficiency and performance.', 'Dallas', 'Texas', 'Finished', 30000000.00, '2024-04-01', '2024-09-01', 6002, 350, 'Large', true);

INSERT INTO Airplane (`Name`, Description, City, State, ProductionStage, Cost, DateStarted, DateFinished, FacilityId, SeatingCapacity, `Size`, HasFirstClass)
VALUES ('Boeing 737', 'A medium-sized, single-aisle airliner with a seating capacity of 225 passengers, known for its reliability and versatility.', 'Miami', 'Florida', 'Unstarted', 120000000.00, '2024-05-01', '2024-10-01', 6003, 225, 'Medium', true);
-- AIRPLANE


-- COMPONENT
INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Engine', 'Jet engine for propulsion', 'Seattle', 'Washington', 'Propulsion', 6001, 1000000.00, 'Unstarted');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Wing', 'Aircraft wing for lift', 'Dallas', 'Texas', 'Structure', 6002, 500000.00, 'In-Progress');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Landing Gear', 'Aircraft landing system', 'Miami', 'Florida', 'System', 6003, 200000.00, 'Unstarted');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Avionics', 'Electronic systems for navigation and communication', 'Chicago', 'Illinois', 'Electronics', 6004, 300000.00, 'Unstarted');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Cockpit', 'Aircraft control and monitoring', 'Los Angeles', 'California', 'System', 6005, 400000.00, 'In-Progress');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Fuel System', 'Aircraft fuel storage and delivery', 'Houston', 'Texas', 'System', 6006, 150000.00, 'Unstarted');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Interior', 'Aircraft cabin and seating', 'New York', 'New York', 'Interior', 6007, 200000.00, 'Finished');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Tail Unstarted', 'Aircraft tail section', 'Atlanta', 'Georgia', 'Structure', 6008, 250000.00, 'In-Progress');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Navigation Lights', 'Aircraft navigation lighting', 'Atlanta', 'Georgia', 'Lighting', 6008, 10000.00, 'Unstarted');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Hydraulic System', 'Aircraft hydraulic systems', 'New York', 'New York', 'System', 6007, 100000.00, 'Finished');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Oxygen System', 'Aircraft oxygen supply', 'Houston', 'Texas', 'System', 6006, 50000.00, 'Unstarted');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Aircraft Tires', 'Aircraft tire set', 'Los Angeles', 'California', 'Tires', 6005, 20000.00, 'In-Progress');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Control Surfaces', 'Aircraft control surfaces', 'Chicago', 'Illinois', 'Structure', 6004, 150000.00, 'In-Progress');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Communication Systems', 'Aircraft communication equipment', 'Miami', 'Florida', 'Electronics', 6003, 250000.00, 'Unstarted');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Seating', 'Aircraft seating', 'Dallas', 'Texas', 'Interior', 6002, 100000.00, 'Unstarted');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Windows', 'Aircraft windows', 'Seattle', 'Washington', 'Windows', 6001, 50000.00, 'Finished');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Emergency Systems', 'Aircraft emergency systems', 'Seattle', 'Washington', 'Safety', 6001, 200000.00, 'Finished');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Engine Controls', 'Aircraft engine control systems', 'Dallas', 'Texas', 'System', 6002, 100000.00, 'Unstarted');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Cargo Bay', 'Aircraft cargo storage', 'Miami', 'Florida', 'Cargo', 6003, 300000.00, 'Finished');

INSERT INTO Component (`Name`, `Description`, City, State, ComponentType, FacilityId, Cost, ProductionStage)
VALUES ('Lavatory', 'Aircraft lavatory facilities', 'Chicago', 'Illinois', 'Interior', 6004, 50000.00, 'Unstarted');
-- COMPONENT

-- FACILITY
INSERT INTO Facility (`Name`, City, State, `Description`, ComponentsInProduction, ComponentsCompleted, ModelsInProduction, ModelsCompleted, EmployeeCount, ManagerId)
VALUES ('Seattle Plant', 'Seattle', 'Washington', 'Main production facility in Seattle', 15, 25, 3, 5, 200, 8001);

INSERT INTO Facility (`Name`, City, State, `Description`, ComponentsInProduction, ComponentsCompleted, ModelsInProduction, ModelsCompleted, EmployeeCount, ManagerId)
VALUES ('Dallas Facility', 'Dallas', 'Texas', 'Secondary facility in Dallas', 10, 20, 2, 4, 150, 8002);

INSERT INTO Facility (`Name`, City, State, `Description`, ComponentsInProduction, ComponentsCompleted, ModelsInProduction, ModelsCompleted, EmployeeCount, ManagerId)
VALUES ('Miami Plant', 'Miami', 'Florida', 'Production facility in Miami', 8, 15, 1, 3, 100, 8003);

INSERT INTO Facility (`Name`, City, State, `Description`, ComponentsInProduction, ComponentsCompleted, ModelsInProduction, ModelsCompleted, EmployeeCount, ManagerId)
VALUES ('Chicago Facility', 'Chicago', 'Illinois', 'Secondary facility in Chicago', 12, 18, 2, 4, 120, 8004);

INSERT INTO Facility (`Name`, City, State, `Description`, ComponentsInProduction, ComponentsCompleted, ModelsInProduction, ModelsCompleted, EmployeeCount, ManagerId)
VALUES ('Los Angeles Plant', 'Los Angeles', 'California', 'Main production facility in Los Angeles', 18, 30, 4, 6, 250, 8005);

INSERT INTO Facility (`Name`, City, State, `Description`, ComponentsInProduction, ComponentsCompleted, ModelsInProduction, ModelsCompleted, EmployeeCount, ManagerId)
VALUES ('Houston Facility', 'Houston', 'Texas', 'Secondary facility in Houston', 10, 20, 3, 5, 180, 8006);

INSERT INTO Facility (`Name`, City, State, `Description`, ComponentsInProduction, ComponentsCompleted, ModelsInProduction, ModelsCompleted, EmployeeCount, ManagerId)
VALUES ('New York Plant', 'New York', 'New York', 'Main production facility in New York', 20, 35, 5, 7, 300, 8007);

INSERT INTO Facility (`Name`, City, State, `Description`, ComponentsInProduction, ComponentsCompleted, ModelsInProduction, ModelsCompleted, EmployeeCount, ManagerId)
VALUES ('Atlanta Facility', 'Atlanta', 'Georgia', 'Secondary facility in Atlanta', 12, 22, 2, 4, 160, 8008);
-- FACILITY

-- MANAGER
INSERT INTO Manager (`Name`, `Password`, Position, AccessLevel, FacilityId)
VALUES ('John Doe', 'password123', 'Manager', 3, 6001);

INSERT INTO Manager (`Name`, `Password`, Position, AccessLevel, FacilityId)
VALUES ('Jane Smith', 'qwerty456', 'Supervisor', 2, 6002);

INSERT INTO Manager (`Name`, `Password`, Position, AccessLevel, FacilityId)
VALUES ('Alice Johnson', 'abc123', 'Assistant', 1, 6003);

INSERT INTO Manager (`Name`, `Password`, Position, AccessLevel, FacilityId)
VALUES ('Bob Brown', 'securepass', 'Manager', 3, 6004);

INSERT INTO Manager (`Name`, `Password`, Position, AccessLevel, FacilityId)
VALUES ('Sarah Williams', 'p@ssw0rd!', 'Supervisor', 2, 6005);

INSERT INTO Manager (`Name`, `Password`, Position, AccessLevel, FacilityId)
VALUES ('Michael Davis', 'managerpass', 'Manager', 3, 6006);

INSERT INTO Manager (`Name`, `Password`, Position, AccessLevel, FacilityId)
VALUES ('Emily Wilson', 'emily123', 'Supervisor', 2, 6007);

INSERT INTO Manager (`Name`, `Password`, Position, AccessLevel, FacilityId)
VALUES ('David Rodriguez', 'davidpass', 'Assistant', 1, 6008);
-- MANAGER
