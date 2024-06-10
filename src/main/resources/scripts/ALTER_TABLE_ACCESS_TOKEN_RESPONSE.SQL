ALTER TABLE access_token_response
ADD COLUMN modification_timestamp TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP;

CREATE TRIGGER update_modification_timestamp
BEFORE UPDATE ON access_token_response
FOR EACH ROW
SET NEW.modification_timestamp = CURRENT_TIMESTAMP;