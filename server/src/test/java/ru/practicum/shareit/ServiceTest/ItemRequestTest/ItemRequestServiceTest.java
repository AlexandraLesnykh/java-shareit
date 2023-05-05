package ru.practicum.shareit.ServiceTest.ItemRequestTest;

import org.junit.jupiter.api.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.exeptions.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.ItemRequestServiceImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.UserServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static ru.practicum.shareit.ServiceTest.ItemTest.ItemServiceTestUtils.getItem;
import static ru.practicum.shareit.ServiceTest.UserTest.UserServiceTestUtils.getUser;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ItemRequestServiceTest {

    private ItemRequestService itemRequestService;
    private ItemRepository itemRepository;
    private UserRepository userRepository;
    private UserService userService;
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void setUp() {
        itemRequestRepository = mock(ItemRequestRepository.class);
        itemRepository = mock(ItemRepository.class);
        userRepository = mock(UserRepository.class);
        userService = new UserServiceImpl(userRepository);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userService, itemRepository);
        getItem(1L).setRequestId(3L);
    }

    @Test
    @Order(1)
    @DisplayName("1.Create test")
    void createTest() {
        Long ownerId = 1L;
        ItemRequest itemRequestListWithoutId = ItemRequestServiceTestUtils.getItemRequestWithoutIds().get(0);
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(getUser(ownerId)));
        when(itemRequestRepository.save(itemRequestListWithoutId)).thenReturn(ItemRequestServiceTestUtils.getItemRequest(1L));
        ItemRequest itemRequest = itemRequestService.create(itemRequestListWithoutId, ownerId);

        Assertions.assertEquals(1, itemRequest.getId());
        Assertions.assertEquals("Need a car", itemRequest.getDescription());
    }

    @Test
    @Order(2)
    @DisplayName("2.Create test fail wrong owner Id")
    void shouldThrowExceptionWhileCreatingWrongOwnerId() {
        Long ownerId = 10L;
        ItemRequest itemRequestListWithoutId = ItemRequestServiceTestUtils.getItemRequestWithoutIds().get(0);
        when(userRepository.findById(ownerId)).thenThrow(new ObjectNotFoundException("Wrong ID"));
        when(itemRequestRepository.save(itemRequestListWithoutId)).thenReturn(ItemRequestServiceTestUtils.getItemRequest(1L));

        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.create(itemRequestListWithoutId, ownerId));
        Assertions.assertEquals("Wrong ID", exception.getMessage());
    }

    @Test
    @Order(3)
    @DisplayName("3.Get all requests")
    void getAllRequestsTest() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        Long ownerId = 3L;
        Page<ItemRequest> page = new PageImpl<>(ItemRequestServiceTestUtils.getItemRequest());
        when(itemRequestRepository.findAll(pageRequest)).thenReturn(page);

        List<ItemRequestDto> itemRequestDtoList = itemRequestService.getAllRequests(ownerId, pageRequest);
        Assertions.assertEquals(3, itemRequestDtoList.size());
    }

    @Test
    @Order(4)
    @DisplayName("4.Get requests by id")
    void getRequestsByIdTest() {
        Long id = 3L;
        Long ownerId = 3L;
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(getUser(id)));
        when(itemRequestRepository.findById(id)).thenReturn(Optional.ofNullable(ItemRequestServiceTestUtils.getItemRequest(id)));
        List<Item> items = List.of(getItem(1L));
        when(itemRepository.findAllByRequestId(id)).thenReturn(items);

        ItemRequestDto itemRequestDto = itemRequestService.getRequestsById(ownerId, id);
        Assertions.assertEquals(3, itemRequestDto.getId());
        Assertions.assertEquals("Need a book", itemRequestDto.getDescription());
    }

    @Test
    @Order(5)
    @DisplayName("5.Get requests by id fail wrong id")
    void shouldThrowExceptionWrongId() {
        Long id = 30L;
        Long ownerId = 3L;
        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(getUser(ownerId)));
        when(itemRequestRepository.findById(id)).thenThrow(new ObjectNotFoundException("Wrong ID"));

        ObjectNotFoundException exception = Assertions.assertThrows(ObjectNotFoundException.class,
                () -> itemRequestService.getRequestsById(ownerId, id));
        Assertions.assertEquals("Wrong ID", exception.getMessage());
    }

    @Test
    @Order(6)
    @DisplayName("6.Get  all requests by owner id")
    void getAllRequestsByOwner() {
        Long id = 3L;
        Long ownerId = 3L;
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<ItemRequest> page = new PageImpl<>(List.of(ItemRequestServiceTestUtils.getItemRequest().get(2)));

        when(userRepository.findById(ownerId)).thenReturn(Optional.ofNullable(getUser(ownerId)));
        when(itemRequestRepository.findAllByRequestorOrderByCreated(ownerId, pageRequest))
                .thenReturn(page);

        ItemRequestDto itemRequestDto = itemRequestService.getAllRequestsByOwner(ownerId, pageRequest).get(0);
        Assertions.assertEquals(3, itemRequestDto.getId());
        Assertions.assertEquals("Need a book", itemRequestDto.getDescription());

    }
}
