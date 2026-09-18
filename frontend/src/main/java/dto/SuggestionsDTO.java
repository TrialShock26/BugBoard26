package dto;

import java.util.ArrayList;
import java.util.List;


public class SuggestionsDTO {

    private boolean eligible;
    private Integer myWorkload;
    private List<IssueDTO> availableIssues = new ArrayList<>();

    public SuggestionsDTO() { }

    public SuggestionsDTO(boolean eligible, Integer myWorkload, List<IssueDTO> availableIssues) {
        this.eligible = eligible;
        this.myWorkload = myWorkload;
        this.availableIssues = availableIssues != null ? availableIssues : new ArrayList<>();
    }

    public boolean isEligible() { return eligible; }
    public void setEligible(boolean eligible) { this.eligible = eligible; }

    public Integer getMyWorkload() { return myWorkload; }
    public void setMyWorkload(Integer myWorkload) { this.myWorkload = myWorkload; }

    public List<IssueDTO> getAvailableIssues() { return availableIssues; }
    public void setAvailableIssues(List<IssueDTO> availableIssues) {
        this.availableIssues = availableIssues != null ? availableIssues : new ArrayList<>();
    }
}
