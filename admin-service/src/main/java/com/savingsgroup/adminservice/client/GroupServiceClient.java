package com.savingsgroup.adminservice.client;

import com.savingsgroup.adminservice.dto.ApiResponse;
import com.savingsgroup.adminservice.dto.GroupDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "group-service")
public interface GroupServiceClient {
    @GetMapping("/api/groups")
    ApiResponse<List<GroupDTO>> getAllGroups();

    @GetMapping("/api/groups/{groupId}")
    ApiResponse<GroupDTO> getGroupById(@PathVariable String groupId);

    @PutMapping("/api/groups/{groupId}/block")
    ApiResponse<Void> blockGroup(@PathVariable String groupId, @RequestBody String reason);

    @PutMapping("/api/groups/{groupId}/unblock")
    ApiResponse<Void> unblockGroup(@PathVariable String groupId);

    @DeleteMapping("/api/groups/{groupId}")
    ApiResponse<Void> deleteGroup(@PathVariable String groupId);
}