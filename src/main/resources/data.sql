-- Sample data for H2 loaded at application startup
-- Table: todos (id IDENTITY, title UNIQUE NOT NULL, description, created_at NOT NULL)

INSERT INTO todos (title, description, completed, created_at) VALUES
  ('Buy groceries', 'Milk, eggs, bread',false, CURRENT_TIMESTAMP()),
  ('Finish report', 'Complete the quarterly report by Friday', false, CURRENT_TIMESTAMP()),
  ('Workout', '30-minute run and stretching', false,CURRENT_TIMESTAMP()),
  ('Read a book', 'Continue reading Clean Code', false, CURRENT_TIMESTAMP()),
  ('Plan weekend', 'Decide on hiking trail and groceries list', false, CURRENT_TIMESTAMP());
