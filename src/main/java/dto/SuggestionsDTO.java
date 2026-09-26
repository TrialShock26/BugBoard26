package dto;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class SuggestionsDTO {

    @Setter private boolean eligible;
    @Setter private Integer myWorkload;
    private List<IssueDTO> availableIssues = new ArrayList<>();

    public SuggestionsDTO() { }

    public SuggestionsDTO(boolean eligible, Integer myWorkload, List<IssueDTO> availableIssues) {
        this.eligible = eligible;
        this.myWorkload = myWorkload;
        this.availableIssues = availableIssues != null ? availableIssues : new ArrayList<>();
    }

    public void setAvailableIssues(List<IssueDTO> availableIssues) {
        this.availableIssues = availableIssues != null ? availableIssues : new ArrayList<>();
    }
}
