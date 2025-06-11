# Flyway Database Migrations

This project uses Flyway for database schema management, providing versioned, repeatable database migrations.

## Migration Files

All migration files are located in `src/main/resources/db/migration/` and follow the naming convention:
`V{version}__{description}.sql`

### Current Migrations

1. **V1__Create_dog_table.sql** - Creates the basic dog table structure with indexes
2. **V2__Insert_sample_dogs.sql** - Inserts initial sample dog data for testing
3. **V3__Add_dog_attributes.sql** - Adds detailed attributes (breed, age, size, etc.) with sample data

## Configuration

Flyway is configured in `application.properties`:

```properties
# Flyway configuration
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.validate-on-migrate=true

# Disable SQL init mode (using Flyway instead)
spring.sql.init.mode=never
```

## Creating New Migrations

1. Create a new file in `src/main/resources/db/migration/`
2. Follow naming convention: `V{next_version}__{description}.sql`
3. Write your SQL DDL/DML statements
4. Restart the application - Flyway will automatically apply new migrations

### Example: Adding a new column

```sql
-- V4__Add_vaccination_status.sql
ALTER TABLE dog ADD COLUMN vaccination_status VARCHAR(50) DEFAULT 'UNKNOWN';
CREATE INDEX idx_dog_vaccination ON dog(vaccination_status);
```

## Monitoring Migrations

- **Flyway Info Endpoint**: `GET /admin/flyway-info` - Shows migration status
- **Database Table**: Check `flyway_schema_history` table for migration history
- **Logs**: Application startup logs show which migrations are applied

## Best Practices

1. **Never modify existing migration files** - Create new ones instead
2. **Test migrations** on development database first
3. **Use descriptive names** for migration files
4. **Include rollback instructions** in comments if needed
5. **Keep migrations small** and focused on single changes

## Rollback Strategy

Flyway doesn't support automatic rollbacks in the community edition. For rollbacks:

1. Create a new migration that reverses the changes
2. Or manually execute rollback SQL statements
3. Document rollback procedures in migration comments

## Troubleshooting

### Migration Checksum Mismatch
If you see checksum errors, it means a migration file was modified after being applied:
```bash
# Fix by repairing the schema history
mvn flyway:repair
```

### Failed Migration
If a migration fails:
1. Fix the SQL in the migration file
2. Manually clean up any partial changes in the database
3. Run `mvn flyway:repair` if needed
4. Restart the application

### Reset Database (Development Only)
To start fresh in development:
```bash
# Drop all objects and rerun all migrations
mvn flyway:clean flyway:migrate
```

## Useful Commands

```bash
# Show migration status
mvn flyway:info

# Validate migrations
mvn flyway:validate

# Repair schema history
mvn flyway:repair

# Clean database (development only!)
mvn flyway:clean
``` 