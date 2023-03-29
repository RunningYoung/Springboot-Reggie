package com.xucl.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.xucl.entity.AddressBook;
import com.xucl.mapper.AddressBookMapper;
import com.xucl.service.AddressBookService;
import org.springframework.stereotype.Service;

/**
 * @author xucl
 * @apiNote
 * @date 2023/3/28 14:28
 */
@Service
public class AddressBookServiceImpl extends ServiceImpl<AddressBookMapper, AddressBook> implements AddressBookService {
}
