-- Sample dog data
INSERT INTO dog (name, owner, description) VALUES
('Prancer', 'Original Owner', 'A neurotic, man-hating, animal-hating, children-hating dog that looks like a gremlin. Lives in a demonic Chihuahua hellscape he has created in our home.'),
('Buddy', 'Jane Doe', 'A friendly golden retriever, loves kids and other dogs. Enjoys long walks in the park.'),
('Lucy', 'John Smith', 'A calm and gentle beagle, great companion for seniors. Enjoys naps and treats.'),
('Max', 'Alice Brown', 'An energetic Jack Russell Terrier, needs a lot of exercise. Very playful and intelligent.')
ON CONFLICT (id) DO NOTHING;
