package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.CommentMapper;
import ru.practicum.shareit.comments.CommentRepository;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.exeptions.ObjectNotFoundException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final JdbcTemplate jdbcTemplate;
    private final ItemRepository repository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    public ItemServiceImpl(JdbcTemplate jdbcTemplate, ItemRepository repository, BookingRepository bookingRepository, CommentRepository commentRepository, UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.repository = repository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Item> search(String text) {
        text = text.toLowerCase();
        boolean textExistInName = false;
        boolean textExistInDescription = false;
        Collection<Item> allItems = findAll();
        Collection<Item> resultBeforeSort = new HashSet<>();
        List<Item> result = new ArrayList<>();
        List<Long> resulIds = new ArrayList<>();
        resultBeforeSort.clear();
        resulIds.clear();
        result.clear();
        for (Item item : allItems) {
            if (!text.isBlank()) {
                textExistInName = item.getName().toLowerCase().contains(text);
                textExistInDescription = item.getDescription().toLowerCase().contains(text);
                if ((textExistInName || textExistInDescription) && item.isAvailable()) {
                    resultBeforeSort.add(item);
                    resulIds.add(item.getId());
                }
            } else {
                return new ArrayList<>();
            }
        }
        resulIds = resulIds.stream()
                .sorted()
                .collect(Collectors.toList());
        for (Long id : resulIds) {
            for (Item item : resultBeforeSort) {
                if (item.getId() == id) {
                    result.add(item);
                }
            }
        }
        return result;
    }

    @Override
    public Item create(Item item, long ownerId) throws ValidationException {
        checkIdWhileCreate(ownerId);
        item.setOwner(ownerId);
        repository.save(item);
        return repository.save(item);
    }

    @Override
    public Item update(ItemDto itemDto, long id, long ownerId) {
        Item item1 = repository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Wrong ID"));
        checkIdWhileUpdate(id, ownerId);
        if ((itemDto.getName() == null && itemDto.getDescription() == null) ||
                (!itemDto.isAvailable() && itemDto.getName() != null && itemDto.getDescription() != null)) {
            item1.setAvailable(itemDto.isAvailable());
        }
        if (itemDto.getName() != null) {
            item1.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            item1.setDescription(itemDto.getDescription());
        }
        return repository.save(item1);
    }

    @Override
    public List<ItemDto> findAll(long ownerId) {
        List<Item> items = new ArrayList<>(repository.findAll());
        List<ItemDto> listForReturn = new ArrayList<>();
        for (Item item : items) {
            if (item.getOwner() == ownerId) {

                listForReturn.add(addBookingsAndCommentToItem(item.getId(), ownerId));
            }
        }
        return listForReturn;
    }

    @Override
    public ItemDto findItem(long id, long ownerId) {
        return addBookingsAndCommentToItem(id, ownerId);
    }

    @Override
    public CommentDto addComment(Comment comment, long itemId, long ownerId) throws ValidationException {
        Booking booking = bookingRepository.findByBookerIdAndItemIdAndEndBefore(ownerId, itemId, LocalDateTime.now());
        try {
            if (booking == null && booking.getItem().getOwner() == ownerId || comment.getText().isEmpty()) {
                throw new ValidationException();
            }
            User user = userRepository.findById(ownerId).orElseThrow(() -> new ObjectNotFoundException("Wrong ID"));
            comment.setAuthorId(ownerId);
            comment.setAuthor(user);
            comment.setItemId(itemId);
            comment.setCreated(LocalDateTime.now());
            commentRepository.save(comment);
        } catch (NullPointerException e) {
            throw new ValidationException();
        }
        return CommentMapper.toCommentDto(comment);
    }

    private ItemDto addBookingsAndCommentToItem(long id, long ownerId) {
        Item item = repository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Wrong ID"));
        if (!bookingRepository.findBookingByItemId(id).isEmpty()) {
            try {
                if (item.getOwner() != ownerId) {
                    throw new NullPointerException();
                }
                Booking lastBooking = bookingRepository.findFirstByItemIdAndStatusAndEndBeforeOrderByEnd(id,
                        BookingStatus.APPROVED, LocalDateTime.now());
                item.setLastBooking(lastBooking);
                Booking nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStart(id,
                        BookingStatus.APPROVED, LocalDateTime.now());
                item.setNextBooking(nextBooking);
            } catch (NullPointerException exception) {
                item.setLastBooking(null);
                item.setNextBooking(null);
            }
        }
        List<Comment> comments = commentRepository.findByAuthorIdAndItemId(ownerId, id);
        if (comments.isEmpty()) {
            comments.addAll(commentRepository.findByItemId(id));
            for (Comment comment : comments) {
                if (item.getOwner() != ownerId) {
                    comments.remove(comment);
                }
            }
        }
        List<CommentDto> commentDtos = new ArrayList<>();
        for (Comment comment : comments) {
            User user = userRepository.findById(comment.getAuthorId()).orElseThrow(() -> new ObjectNotFoundException("Wrong ID"));
            comment.setAuthor(user);
            comment.setAuthorId(user.getId());
            comment.setItemId(id);
            comment.setCreated(LocalDateTime.now());
            commentDtos.add(CommentMapper.toCommentDto(comment));
        }
        item.setComments(commentDtos);

        return ItemMapper.toItemDto(item);
    }

    private void checkIdWhileCreate(long ownerId) {
        int checkUserId = jdbcTemplate.queryForObject("SELECT COUNT (id) FROM users WHERE id=?", Integer.class, ownerId);
        if (checkUserId == 0) {
            throw new ObjectNotFoundException("Wrong owner id");
        }
    }

    private void checkIdWhileUpdate(long id, long ownerId) {
        long checkUserId = jdbcTemplate.queryForObject("SELECT owner FROM items WHERE id=?", Long.class, id);
        if (checkUserId != ownerId) {
            throw new ObjectNotFoundException("Wrong owner id");
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

    private Item getItemBD(SqlRowSet itemRows) {
        Item itemSql = new Item();
        itemSql.setId(itemRows.getLong("id"));
        itemSql.setName(itemRows.getString("name"));
        itemSql.setDescription(itemRows.getString("description"));
        itemSql.setAvailable(itemRows.getBoolean("available"));
        return itemSql;
    }
}
