package ru.practicum.shareit.item;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.comments.CommentMapper;
import ru.practicum.shareit.comments.CommentRepository;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.exeptions.ObjectNotFoundException;
import ru.practicum.shareit.exeptions.BadRequestException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
@Service
public class ItemServiceImpl implements ItemService {

    private final ItemRepository repository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;

    public ItemServiceImpl(ItemRepository repository, BookingRepository bookingRepository, CommentRepository commentRepository, UserService userService) {
        this.repository = repository;
        this.bookingRepository = bookingRepository;
        this.commentRepository = commentRepository;
        this.userService = userService;
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
    public Item create(Item item, long ownerId) throws BadRequestException {
        User checkUser = userService.findUser(ownerId);
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
    public List<Item> findAll(long ownerId) {
        List<Item> items = new ArrayList<>(repository.findAll());
        List<Item> list = new ArrayList<>();
        List<Item> listForReturn = new ArrayList<>();
        List<Long> ids = new ArrayList<>();
        for (Item item : items) {
            if (item.getOwner() == ownerId) {
                list.add(addBookingsAndCommentToItem(item.getId(), ownerId));
                ids.add(item.getId());
            }
        }
        ids = ids.stream()
                .sorted()
                .collect(Collectors.toList());
        for (Long id : ids) {
            for (Item item : list) {
                if (item.getId() == id) {
                    listForReturn.add(item);
                }
            }
        }
        return listForReturn;
    }

    @Override
    public Item findItem(long id, long ownerId) {
        return addBookingsAndCommentToItem(id, ownerId);
    }

    @Override
    public CommentDto addComment(Comment comment, long itemId, long ownerId) throws BadRequestException {
        if (comment.getText().isEmpty()) {
            throw new BadRequestException("Text shouldn't be empty");
        }
        User user = userService.findUser(ownerId);
        Optional<Booking> booking = bookingRepository.findFirstByBookerIdAndItemIdAndEndBefore(ownerId, itemId, LocalDateTime.now());
        if (booking.isEmpty()) {
            throw new BadRequestException("Wrong id");
        }
        comment.setAuthorId(ownerId);
        comment.setAuthor(user);
        comment.setItemId(itemId);
        comment.setCreated(LocalDateTime.now());
        Comment comment1 = commentRepository.save(comment);
        CommentDto commentDto = CommentMapper.toCommentDto(comment1);
        commentDto.setAuthorName(user.getName());
        return commentDto;
    }

    private Item addBookingsAndCommentToItem(long id, long ownerId) {
        Item item = repository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Wrong ID"));
        if (!bookingRepository.findBookingByItemId(id).isEmpty()) {
            Optional<Booking> lastBooking = bookingRepository.findFirstByItemIdAndStatusAndStartBeforeOrderByStartDesc(id,
                    BookingStatus.APPROVED, LocalDateTime.now());
            Optional<Booking> nextBooking = bookingRepository.findFirstByItemIdAndStatusAndStartAfterOrderByStart(id,
                    BookingStatus.APPROVED, LocalDateTime.now());
            if (item.getOwner() != ownerId) {
                item.setLastBooking(null);
                item.setNextBooking(null);
            } else {
                if (lastBooking.isEmpty()) {
                    item.setLastBooking(null);
                } else {
                    item.setLastBooking(lastBooking.get());
                }
                if (nextBooking.isEmpty()) {
                    item.setNextBooking(null);
                } else {
                    item.setNextBooking(nextBooking.get());
                }
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
            User user = userService.findUser(comment.getAuthorId());
            comment.setAuthor(user);
            comment.setAuthorId(user.getId());
            comment.setItemId(id);
            comment.setCreated(LocalDateTime.now());
            commentDtos.add(CommentMapper.toCommentDto(comment));
        }
        item.setComments(commentDtos);
        return item;
    }

    private void checkIdWhileUpdate(long id, long ownerId) {
        Item item = repository.findById(id).get();
        if (item.getOwner() != ownerId) {
            throw new ObjectNotFoundException("Wrong owner id");
        }
    }

    private List<Item> findAll() {
        return repository.findAll();
    }

    @Override
    public Item findById(long id) {
        return repository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Wrong id"));
    }

}
