package ru.practicum.shareit.request;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exeptions.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    public ItemRequestServiceImpl(ItemRequestRepository repository, UserService userService, ItemRepository itemRepository) {
        this.repository = repository;
        this.userService = userService;
        this.itemRepository = itemRepository;
    }

    @Override
    public ItemRequest create(ItemRequest itemRequest, long ownerId) {
        User checkUser = userService.findUser(ownerId);
        itemRequest.setRequestor(ownerId);
        itemRequest.setCreated(LocalDateTime.now());
        return repository.save(itemRequest);
    }

    @Override
    public List<ItemRequestDto> getAllRequestsByOwner(long ownerId, PageRequest pageRequest) {
        User checkUser = userService.findUser(ownerId);
        Page<ItemRequest> page = repository.findAllByRequestorOrderByCreated(ownerId, pageRequest);
        return getItemRequestDtos(page);
    }

    @Override
    public List<ItemRequestDto> getAllRequests(long ownerId, PageRequest pageRequest) {
        Page<ItemRequest> page = repository.findAll(pageRequest);
        return getItemRequestDtos(page);
    }

    @Override
    public ItemRequestDto getRequestsById(long ownerId, long id) {
        User checkUser = userService.findUser(ownerId);
        ItemRequest itemRequest = repository.findById(id).orElseThrow(() -> new ObjectNotFoundException("Wrong ID"));
        itemRequest.setItems(itemRepository.findAllByRequestId(id));

        return ItemRequestMapper.toItemRequestDto(itemRequest);
    }

    private List<ItemRequestDto> getItemRequestDtos(Page<ItemRequest> page) {
        List<ItemRequest> list = page.getContent();
        List<ItemRequestDto> dtoList = new ArrayList<>();
        for (ItemRequest itemRequest : list) {
            itemRequest.setItems(itemRepository.findAllByRequestId(itemRequest.getId()));
            dtoList.add(ItemRequestMapper.toItemRequestDto(itemRequest));
        }
        return dtoList;
    }
}
