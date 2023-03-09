package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exeptions.ObjectNotFoundException;
import ru.practicum.shareit.exeptions.ValidationException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final UserRepository userRepository;

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemService itemService;

    public BookingServiceImpl(BookingRepository repository, UserRepository userRepository, ItemRepository itemRepository, UserService userService, ItemService itemService) {
        this.repository = repository;
        this.userRepository = userRepository;
        this.itemRepository = itemRepository;
        this.userService = userService;
        this.itemService = itemService;
    }

    @Override
    public BookingDto create(Booking booking, long ownerId) throws ValidationException {
        checkIdsWhileCreate(booking, ownerId);
        checkAvailable(booking);
        booking.setItem(itemService.findItem(booking.getItemId(), ownerId));
        if (booking.getItem().getOwner() == ownerId) {
            throw new ObjectNotFoundException("Wrong id");
        }
        booking.setStatus(BookingStatus.WAITING);
        booking.setBookerId(ownerId);
        booking.setUser(userService.findUser(booking.getBookerId()));
        Booking bookingNew = repository.save(booking);
        return BookingMapper.toBookingDto(bookingNew);
    }

    @Override
    public BookingDto update(long bookingId, boolean approved, long ownerId) throws ValidationException {
        Booking booking = repository.findById(bookingId).orElseThrow(() -> new ObjectNotFoundException("Wrong id"));
        booking.setUser(userService.findUser(booking.getBookerId()));
        booking.setItem(itemService.findById(booking.getItemId()));
        BookingDto bookingDto = BookingMapper.toBookingDto(booking);
        if (bookingDto.getBooker().getId() == ownerId) {
            throw new ObjectNotFoundException("Wrong id");
        }
        if (bookingDto.getStatus() == BookingStatus.APPROVED && approved || bookingDto.getItem().getOwner() != ownerId) {
            throw new ValidationException("Wrong id");
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return BookingMapper.toBookingDto(repository.save(booking));
    }

    @Override
    public BookingDto getBookingById(long bookingId, long ownerId) {
        Booking booking = repository.findById(bookingId).orElseThrow(() -> new ObjectNotFoundException("Wrong ID"));
        booking.setUser(userService.findUser(booking.getBookerId()));
        booking.setItem(itemService.findById(booking.getItemId()));
        if (booking.getItem().getOwner() == ownerId || booking.getBookerId() == ownerId) {
            return BookingMapper.toBookingDto(booking);
        } else {
            throw new ObjectNotFoundException("Wrong id");
        }
    }

    @Override
    public List<BookingDto> findAll(String state, long ownerId) throws ValidationException {
        List<BookingDto> bookingDtos = findAllGeneral(state, ownerId);
        List<BookingDto> listForReturn = new ArrayList<>();
        listForReturn.clear();
        for (BookingDto bookingDto : bookingDtos) {
            if (bookingDto.getBooker().getId() == ownerId) {
                listForReturn.add(bookingDto);
            }
        }
        return listForReturn;
    }

    @Override
    public List<BookingDto> findAllWithOwner(String state, long ownerId) throws ValidationException {
        List<BookingDto> bookingDtos = findAllGeneral(state, ownerId);
        List<BookingDto> listForReturn = new ArrayList<>();
        for (BookingDto bookingDto : bookingDtos) {
            if (bookingDto.getItem().getOwner() == ownerId) {
                listForReturn.add(bookingDto);
            }
        }
        return listForReturn;
    }

    private List<BookingDto> findAllGeneral(String state, long ownerId) throws ValidationException {
        List<Booking> bookings = new ArrayList<>(repository.findAll(Sort.by(Sort.Direction.DESC, "start")));
        List<BookingDto> bookingDtos = new ArrayList<>();
        List<BookingDto> bookingForReturn = new ArrayList<>();
        for (Booking booking : bookings) {
            checkIdsWhileCreate(booking, ownerId);
            booking.setUser(userService.findUser(booking.getBookerId()));
            booking.setItem(itemService.findById(booking.getItemId()));
            bookingDtos.add(BookingMapper.toBookingDto(booking));
        }
        switch (state) {
            case "ALL":
                bookingForReturn.addAll(bookingDtos);
            case "CURRENT":
                for (BookingDto bookingDto : bookingDtos) {
                    if (bookingDto.getStart().isBefore(LocalDateTime.now()) &&
                            bookingDto.getEnd().isAfter(LocalDateTime.now())) {
                        bookingForReturn.add(bookingDto);
                    }
                }
                break;
            case "FUTURE":
                for (BookingDto bookingDto : bookingDtos) {
                    if (bookingDto.getStart().isAfter(LocalDateTime.now())) {
                        bookingForReturn.add(bookingDto);
                    }
                }
                break;
            case "PAST":
                for (BookingDto bookingDto : bookingDtos) {
                    if (bookingDto.getEnd().isBefore(LocalDateTime.now())) {
                        bookingForReturn.add(bookingDto);
                    }
                }
                break;
            case "WAITING":
                for (BookingDto bookingDto : bookingDtos) {
                    if (bookingDto.getStatus().equals(BookingStatus.WAITING)) {
                        bookingForReturn.add(bookingDto);
                    }
                }
                break;
            case "REJECTED":
                for (BookingDto bookingDto : bookingDtos) {
                    if (bookingDto.getStatus().equals(BookingStatus.REJECTED)) {
                        bookingForReturn.add(bookingDto);
                    }
                }
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookingForReturn;
    }

    private void checkIdsWhileCreate(Booking booking, long ownerId) {
        User user = userRepository.findById(ownerId).orElseThrow(() -> new javax.validation.ValidationException("Wrong ID"));
        Item item = itemService.findById(booking.getItemId());
    }

    private void checkAvailable(Booking booking) throws ValidationException {
        boolean checkAvailable = itemService.findById(booking.getItemId()).isAvailable();
        if (!checkAvailable || booking.getEnd().isBefore(booking.getStart())) {
            throw new ValidationException("The item should be available");
        }
    }
}
