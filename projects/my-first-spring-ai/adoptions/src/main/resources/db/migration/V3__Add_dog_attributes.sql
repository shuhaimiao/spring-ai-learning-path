-- V3: Add additional dog attributes
-- This migration adds more detailed attributes for better matching

ALTER TABLE dog ADD COLUMN IF NOT EXISTS breed VARCHAR(100);
ALTER TABLE dog ADD COLUMN IF NOT EXISTS age_years INTEGER;
ALTER TABLE dog ADD COLUMN IF NOT EXISTS size VARCHAR(20) CHECK (size IN ('SMALL', 'MEDIUM', 'LARGE', 'EXTRA_LARGE'));
ALTER TABLE dog ADD COLUMN IF NOT EXISTS energy_level VARCHAR(20) CHECK (energy_level IN ('LOW', 'MEDIUM', 'HIGH'));
ALTER TABLE dog ADD COLUMN IF NOT EXISTS good_with_kids BOOLEAN DEFAULT true;
ALTER TABLE dog ADD COLUMN IF NOT EXISTS good_with_cats BOOLEAN DEFAULT true;
ALTER TABLE dog ADD COLUMN IF NOT EXISTS good_with_dogs BOOLEAN DEFAULT true;
ALTER TABLE dog ADD COLUMN IF NOT EXISTS adoption_fee DECIMAL(8,2);
ALTER TABLE dog ADD COLUMN IF NOT EXISTS location VARCHAR(100);
ALTER TABLE dog ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE dog ADD COLUMN IF NOT EXISTS updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP;

-- Create indexes for new searchable fields
CREATE INDEX IF NOT EXISTS idx_dog_breed ON dog(breed);
CREATE INDEX IF NOT EXISTS idx_dog_size ON dog(size);
CREATE INDEX IF NOT EXISTS idx_dog_location ON dog(location);
CREATE INDEX IF NOT EXISTS idx_dog_adoption_fee ON dog(adoption_fee);

-- Update existing dogs with sample data
UPDATE dog SET 
    breed = CASE 
        WHEN name = 'Prancer' THEN 'Chihuahua'
        WHEN name = 'Buddy' THEN 'Golden Retriever'
        WHEN name = 'Lucy' THEN 'Beagle'
        WHEN name = 'Max' THEN 'Jack Russell Terrier'
        WHEN name = 'Luna' THEN 'Siberian Husky'
        WHEN name = 'Charlie' THEN 'German Shepherd'
        WHEN name = 'Bella' THEN 'Labrador Mix'
        WHEN name = 'Rocky' THEN 'Rottweiler Mix'
        ELSE 'Mixed Breed'
    END,
    age_years = CASE 
        WHEN name IN ('Prancer', 'Lucy') THEN 5
        WHEN name IN ('Buddy', 'Charlie') THEN 3
        WHEN name IN ('Max', 'Luna') THEN 2
        WHEN name IN ('Bella', 'Rocky') THEN 4
        ELSE 3
    END,
    size = CASE 
        WHEN name = 'Prancer' THEN 'SMALL'
        WHEN name IN ('Lucy', 'Max') THEN 'MEDIUM'
        WHEN name IN ('Buddy', 'Luna', 'Charlie', 'Bella', 'Rocky') THEN 'LARGE'
        ELSE 'MEDIUM'
    END,
    energy_level = CASE 
        WHEN name IN ('Max', 'Luna', 'Rocky') THEN 'HIGH'
        WHEN name IN ('Lucy', 'Bella') THEN 'LOW'
        ELSE 'MEDIUM'
    END,
    good_with_kids = CASE 
        WHEN name = 'Prancer' THEN false
        ELSE true
    END,
    good_with_cats = CASE 
        WHEN name IN ('Prancer', 'Luna') THEN false
        ELSE true
    END,
    adoption_fee = CASE 
        WHEN size = 'SMALL' THEN 250.00
        WHEN size = 'MEDIUM' THEN 300.00
        WHEN size = 'LARGE' THEN 350.00
        ELSE 300.00
    END,
    location = CASE 
        WHEN name IN ('Prancer', 'Buddy') THEN 'New York City'
        WHEN name IN ('Lucy', 'Max') THEN 'San Francisco'
        WHEN name IN ('Luna', 'Charlie') THEN 'Tokyo'
        WHEN name IN ('Bella', 'Rocky') THEN 'London'
        ELSE 'Barcelona'
    END
WHERE breed IS NULL; 