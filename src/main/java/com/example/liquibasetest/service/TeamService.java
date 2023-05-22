package com.example.liquibasetest.service;

import com.example.liquibasetest.dto.TeamDTO;
import com.example.liquibasetest.entity.Team;
import com.example.liquibasetest.repository.TeamRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class TeamService {

    private final TeamRepository teamRepository;

    public TeamService(TeamRepository teamRepository) {
        this.teamRepository = teamRepository;
    }

    public List<TeamDTO> getAllTeams() {
        List<Team> teams = teamRepository.findAll();
        return teams.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    public TeamDTO getTeamById(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + id));
        return convertToDto(team);
    }

    public TeamDTO createTeam(TeamDTO teamDTO) {
        Team team = convertToEntity(teamDTO);
        Team savedTeam = teamRepository.save(team);
        return convertToDto(savedTeam);
    }

    public TeamDTO updateTeam(Long id, TeamDTO teamDTO) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + id));
        team.setName(teamDTO.getName());
        Team updatedTeam = teamRepository.save(team);
        return convertToDto(updatedTeam);
    }

    public void deleteTeam(Long id) {
        Team team = teamRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Team not found with id: " + id));
        teamRepository.delete(team);
    }

    private TeamDTO convertToDto(Team team) {
        TeamDTO teamDto = new TeamDTO();
        teamDto.setId(team.getId());
        teamDto.setName(team.getName());
        return teamDto;
    }

    private Team convertToEntity(TeamDTO teamDTO) {
        Team team = new Team();
        team.setName(teamDTO.getName());
        return team;
    }
}
