package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.ObjectNotFoundException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;


@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private int generator = 0;

    private List<Integer> users = new ArrayList<>();

    private final JdbcTemplate jdbcTemplate;

    public ItemServiceImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        maxId();
    }

    @Override
    public List<Item> findAll(int ownerId) {
        SqlRowSet itemRows = jdbcTemplate.queryForRowSet("SELECT * FROM items WHERE owner=?", ownerId);
        List<Item> itemSQL = new ArrayList<>();
        while (itemRows.next()) {
            itemSQL.add(getItemBD(itemRows));
        }
        return itemSQL;
    }

    @Override
    public Item findItem(int id) {
        SqlRowSet itemRows = jdbcTemplate.queryForRowSet("SELECT * FROM items WHERE item_id= ?", id);
        itemRows.next();
        if (itemRows.last()) {
            return getItemBD(itemRows);
        } else {
            return null;
        }
    }

    @Override
    public Item create(Item item, int ownerId) throws ValidationException {

        int checkUserId = jdbcTemplate.queryForObject("SELECT COUNT(user_id) FROM users WHERE user_id=?", Integer.class, ownerId);
        if (checkUserId == 0) {
            throw new ObjectNotFoundException("Wrong owner id");
        }
        if (item.getName() == null || item.getDescription() == null || !item.isAvailable()) {
            throw new ValidationException("Wrong request");
        }
        addItem(item);
        jdbcTemplate.update("INSERT INTO items VALUES (?,?,?,?,?,?)", item.getId(), item.getName(), item.getDescription(),
                item.isAvailable(), ownerId, item.getRequest());
        SqlRowSet itemRows = jdbcTemplate.queryForRowSet("SELECT * FROM items WHERE item_id= ?", item.getId());
        itemRows.next();
        return getItemBD(itemRows);
    }

    @Override
    public Item update(ItemDto itemDto, int id, int ownerId) {
        int checkUserId = jdbcTemplate.queryForObject("SELECT owner FROM items WHERE item_id=?", Integer.class, id);
        if (checkUserId != ownerId) {
            throw new ObjectNotFoundException("Wrong owner id");
        }
        if ((itemDto.getName() == null && itemDto.getDescription() == null) ||
                (!itemDto.isAvailable() && itemDto.getName() != null && itemDto.getDescription() != null)) {

            jdbcTemplate.update("UPDATE items SET available=? WHERE item_id=?",
                    itemDto.isAvailable(), id);
        }
        if (itemDto.getName() != null) {
            jdbcTemplate.update("UPDATE items SET name=? WHERE item_id=?",
                    itemDto.getName(), id);
        }
        if (itemDto.getDescription() != null) {
            jdbcTemplate.update("UPDATE items SET description=? WHERE item_id=?",
                    itemDto.getDescription(), id);
        }
        SqlRowSet itemRows = jdbcTemplate.queryForRowSet("SELECT * FROM items WHERE item_id= ?", id);
        if (itemRows.next()) {
            return getItemBD(itemRows);
        } else {
            return null;
        }
    }

    @Override
    public Collection<Item> search(String text) {
        text = text.toLowerCase();
        boolean textExistInName = false;
        boolean textExistInDescription = false;
        Collection<Item> allItems = findAll();
        Collection<Item> result = new HashSet<>();
        result.clear();
        for (Item item : allItems) {
            if (!text.equals("")) {
                textExistInName = item.getName().toLowerCase().contains(text);
                textExistInDescription = item.getDescription().toLowerCase().contains(text);
                if ((textExistInName || textExistInDescription) && item.isAvailable()) {
                    result.add(item);
                }
            } else {
                return new HashSet<>();
            }
        }
        return result;
    }

    private void maxId() {
        Integer i = jdbcTemplate.queryForObject("SELECT MAX(item_id) FROM items", Integer.class);
        if (i == null) {
            generator = 0;
        } else {
            generator = i;
        }
    }

    private List<Item> findAll() {
        SqlRowSet itemRows = jdbcTemplate.queryForRowSet("SELECT * FROM items");
        List<Item> itemSQL = new ArrayList<>();
        while (itemRows.next()) {
            itemSQL.add(getItemBD(itemRows));
        }
        return itemSQL;
    }

    private void addItem(Item item) {
        if (item.getId() == 0) {
            item.setId(++generator);
        }
    }

    private Item getItemBD(SqlRowSet itemRows) {
        Item itemSql = new Item();
        itemSql.setId(itemRows.getInt("item_id"));
        itemSql.setName(itemRows.getString("name"));
        itemSql.setDescription(itemRows.getString("description"));
        itemSql.setAvailable(itemRows.getBoolean("available"));
        return itemSql;
    }


}
