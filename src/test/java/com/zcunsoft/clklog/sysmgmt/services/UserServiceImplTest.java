package com.zcunsoft.clklog.sysmgmt.services;

import com.zcunsoft.clklog.sysmgmt.cfg.AdminNameProperties;
import com.zcunsoft.clklog.sysmgmt.domains.User;
import com.zcunsoft.clklog.sysmgmt.models.enums.ErrorCode;
import com.zcunsoft.clklog.sysmgmt.models.response.ResponseBase;
import com.zcunsoft.clklog.sysmgmt.repository.IUserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.context.MessageSource;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserServiceImplTest {

    @Mock
    private ICodeService codeService;

    @Mock
    private IUserRepository userRepository;

    @Mock
    private MessageSource codeMessageSource;

    @InjectMocks
    private CodeServiceImpl realCodeService;

    private AdminNameProperties adminNameProperties;

    private UserServiceImpl userService;

    private User adminUser;
    private User clklogUser;
    private User normalUser;

    @BeforeEach
    void setUp() {
        adminNameProperties = new AdminNameProperties();
        adminNameProperties.setAdminName(new HashSet<>(Arrays.asList("clklog", "admin")));

        realCodeService = new CodeServiceImpl(codeMessageSource);
        userService = new UserServiceImpl(realCodeService, userRepository, adminNameProperties);

        adminUser = new User();
        adminUser.setUserId("admin-001");
        adminUser.setUserName("admin");

        clklogUser = new User();
        clklogUser.setUserId("clklog-001");
        clklogUser.setUserName("clklog");

        normalUser = new User();
        normalUser.setUserId("normal-001");
        normalUser.setUserName("alice");

        when(codeMessageSource.getMessage(eq("code.Success"), any(), any(Locale.class))).thenReturn("成功");
        when(codeMessageSource.getMessage(eq("code.Failed"), any(), any(Locale.class))).thenReturn("失败");
        when(codeMessageSource.getMessage(eq("code.Forbidden"), any(), any(Locale.class))).thenReturn("无权操作");
    }

    @Test
    @DisplayName("USER-009 delete normal user returns Success and calls repository delete")
    void deleteNormalUserShouldSucceed() {
        String userId = "normal-001";
        when(userRepository.findById(userId)).thenReturn(Optional.of(normalUser));

        ResponseBase<Boolean> resp = userService.delete(userId);

        assertNotNull(resp);
        assertEquals(ErrorCode.Success, resp.getCode());
        assertEquals("成功", resp.getMsg());
        assertTrue(resp.getData());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, times(1)).delete(normalUser);
    }

    @Test
    @DisplayName("USER-010 delete admin returns Forbidden and never calls repository delete")
    void deleteAdminShouldReturnForbidden() {
        String userId = "admin-001";
        when(userRepository.findById(userId)).thenReturn(Optional.of(adminUser));

        ResponseBase<Boolean> resp = userService.delete(userId);

        assertNotNull(resp);
        assertEquals(ErrorCode.Forbidden, resp.getCode());
        assertEquals("无权操作", resp.getMsg());
        assertFalse(resp.getData());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).delete(any(User.class));
    }

    @Test
    @DisplayName("USER-011 delete clklog returns Forbidden and never calls repository delete")
    void deleteClklogShouldReturnForbidden() {
        String userId = "clklog-001";
        when(userRepository.findById(userId)).thenReturn(Optional.of(clklogUser));

        ResponseBase<Boolean> resp = userService.delete(userId);

        assertNotNull(resp);
        assertEquals(ErrorCode.Forbidden, resp.getCode());
        assertEquals("无权操作", resp.getMsg());
        assertFalse(resp.getData());
        verify(userRepository, times(1)).findById(userId);
        verify(userRepository, never()).delete(any(User.class));
    }
}
