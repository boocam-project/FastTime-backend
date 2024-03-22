package com.fasttime.domain.reference.service;


import com.fasttime.domain.reference.entity.Activity;
import com.fasttime.domain.reference.entity.Competition;
import com.fasttime.domain.reference.entity.RecruitmentStatus;
import com.fasttime.domain.reference.entity.SearchCrawling;
import com.fasttime.domain.reference.repository.ActivityRepository;
import com.fasttime.domain.reference.repository.CompetitionRepository;
import com.fasttime.global.util.WebDriverUtil;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class CrawlingService {

    private final WebDriverUtil webDriverUtil;
    private final ActivityRepository activityRepository;
    private final CompetitionRepository competitionRepository;
    private final String activityBaseUrl = "https://linkareer.com/list/activity?filterBy_interestIDs=13&filterType=INTEREST&orderBy_direction=DESC&orderBy_field=CREATED_AT&page=";
    private final String competitionBaseUrl = "https://linkareer.com/list/contest?filterBy_categoryIDs=35&filterType=CATEGORY&orderBy_direction=DESC&orderBy_field=CREATED_AT&page=";

    public void updateNewCompetition() throws InterruptedException {
        WebDriver chromeDriver = webDriverUtil.getChromeDriver();
        searchNewReference(1, chromeDriver, SearchCrawling.COMPETITION);
        chromeDriver.quit();
    }

    public void updateNewActivity() throws InterruptedException {
        WebDriver chromeDriver = webDriverUtil.getChromeDriver();
        searchNewReference(1, chromeDriver, SearchCrawling.ACTIVITY);
        searchNewReference(2, chromeDriver, SearchCrawling.ACTIVITY);
        chromeDriver.quit();
    }

    public void updateDoneCompetition() throws InterruptedException {
        WebDriver chromeDriver = webDriverUtil.getChromeDriver();
        List<Integer> pages = initPage(chromeDriver, SearchCrawling.COMPETITION);
        List<String> recruitingTitles = getRecruitingTitles(pages, chromeDriver,
            SearchCrawling.COMPETITION);

        chromeDriver.quit();

        List<Competition> competitions = competitionRepository.findAllByStatus(
            RecruitmentStatus.DURING);
        for (Competition competition : competitions) {
            if (!recruitingTitles.contains(competition.getTitle())) {
                competition.statusUpdate(RecruitmentStatus.CLOSED);
            }
        }
    }

    public void updateDoneActivity() throws InterruptedException {
        WebDriver chromeDriver = webDriverUtil.getChromeDriver();
        List<Integer> pages = initPage(chromeDriver, SearchCrawling.ACTIVITY);
        List<String> recruitingTitles = getRecruitingTitles(pages, chromeDriver,
            SearchCrawling.ACTIVITY);

        chromeDriver.quit();

        List<Activity> activities = activityRepository.findAllByStatus(RecruitmentStatus.DURING);
        for (Activity activity : activities) {
            if (!recruitingTitles.contains(activity.getTitle())) {
                activity.statusUpdate(RecruitmentStatus.CLOSED);
            }
        }
    }

    public void initActivity() throws InterruptedException {
        WebDriver chromeDriver = webDriverUtil.getChromeDriver();

        List<Integer> pages = initPage(chromeDriver, SearchCrawling.ACTIVITY);

        for (Integer page : pages) {
            searchAllReference(page, chromeDriver, SearchCrawling.ACTIVITY);
        }
        chromeDriver.quit();
    }

    public void initCompetition() throws InterruptedException {
        competitionRepository.deleteAll();

        WebDriver chromeDriver = webDriverUtil.getChromeDriver();
        List<Integer> pages = initPage(chromeDriver, SearchCrawling.COMPETITION);

        for (Integer page : pages) {
            searchAllReference(page, chromeDriver, SearchCrawling.COMPETITION);
        }
        chromeDriver.quit();
    }

    private void searchNewReference(Integer page, WebDriver chromeDriver,
        SearchCrawling searchCrawling) throws InterruptedException {
        String baseUrl = choiceEntity(searchCrawling);
        chromeDriver.get(baseUrl.concat(page.toString()));

        Thread.sleep(1000);

        List<String> urls = chromeDriver.findElements(By.className("image-link")).stream()
            .map(webElement -> webElement.getAttribute("href")).toList();

        if (searchCrawling.equals(SearchCrawling.ACTIVITY)) {
            for (String url : urls) {
                chromeDriver.get(url);
                String title = chromeDriver.findElement(By.className("title")).getText();
                if (activityRepository.existsByTitle(title)) {
                    break;
                }
                creatActivity(chromeDriver);
            }
        }

        if (searchCrawling.equals(SearchCrawling.COMPETITION)) {
            for (String url : urls) {
                chromeDriver.get(url);
                String title = chromeDriver.findElement(By.className("title")).getText();
                if (competitionRepository.existsByTitle(title)) {
                    break;
                }
                creatCompetition(chromeDriver);
            }
        }
    }

    private void searchAllReference(Integer page, WebDriver chromeDriver,
        SearchCrawling searchCrawling)
        throws InterruptedException {
        String baseUrl = choiceEntity(searchCrawling);
        chromeDriver.get(baseUrl.concat(page.toString()));

        Thread.sleep(1000);

        List<String> urls = chromeDriver.findElements(By.className("image-link")).stream()
            .map(webElement -> webElement.getAttribute("href")).toList();

        if (searchCrawling.equals(SearchCrawling.ACTIVITY)) {
            for (String url : urls) {
                chromeDriver.get(url);
                creatActivity(chromeDriver);
            }
        }

        if (searchCrawling.equals(SearchCrawling.COMPETITION)) {
            for (String url : urls) {
                chromeDriver.get(url);
                creatCompetition(chromeDriver);
            }
        }
    }

    private void creatActivity(WebDriver chromeDriver) {
        String title = chromeDriver.findElement(By.className("title")).getText();
        String organizationName = chromeDriver.findElement(By.className("organization-name"))
            .getText();
        WebElement element = chromeDriver.findElement(
            By.className("field-container"));
        String[] activityFields = element.getText().split("\n");
        String corporateType = activityFields[1];
        String participate = activityFields[3];
        String[] registerPeriod = activityFields[5].split(" ~ ");
        LocalDate startDate = dateFormatter(registerPeriod[0]);
        LocalDate endDate = dateFormatter(registerPeriod[1]);
        String period = activityFields[7];
        int recruitment = Integer.parseInt
            (activityFields[9].substring(0, activityFields[9].length() - 1));
        String area = activityFields[11];
        String preferredSkills = activityFields[13];
        String homepageUrl = activityFields[15];
        String activityBenefit = activityFields[17];
        String activityField = activityFields[21];
        String bonusBenefit = activityFields[23];
        RecruitmentStatus status = setStatus(startDate);
        String imageUrl = chromeDriver.findElement(By.className("card-image")).getAttribute("src");
        String description = chromeDriver.findElement(By.className("responsive-element")).getText();

        Activity activity = Activity.builder().title(title)
            .organization(organizationName)
            .corporateType(corporateType)
            .participate(participate)
            .startDate(startDate)
            .endDate(endDate)
            .period(period)
            .recruitment(recruitment)
            .area(area)
            .preferredSkill(preferredSkills)
            .homepageUrl(homepageUrl)
            .activityBenefit(activityBenefit)
            .field(activityField)
            .bonusBenefit(bonusBenefit)
            .imageUrl(imageUrl)
            .description(description)
            .status(status).build();

        activityRepository.save(activity);
    }

    private void creatCompetition(WebDriver chromeDriver) {

        String title = chromeDriver.findElement(By.className("title")).getText();
        String organizationName = chromeDriver.findElement(By.className("organization-name"))
            .getText();
        WebElement element = chromeDriver.findElement(
            By.className("field-container"));
        String[] competitionFields = element.getText().split("\n");
        String corporateType = competitionFields[1];
        String participate = competitionFields[3];
        String awardScale = competitionFields[5];
        String[] registerPeriod = competitionFields[7].split(" ~ ");
        LocalDate startDate = dateFormatter(registerPeriod[0]);
        LocalDate endDate = dateFormatter(registerPeriod[1]);
        String homepageUrl = competitionFields[9];
        String activityBenefit = competitionFields[11];
        String bonusBenefit = competitionFields[15];
        RecruitmentStatus status = setStatus(startDate);
        String imageUrl = chromeDriver.findElement(By.className("card-image")).getAttribute("src");
        String description = chromeDriver.findElement(By.className("responsive-element")).getText();

        Competition competition = Competition.builder()
            .title(title)
            .organization(organizationName)
            .corporateType(corporateType)
            .participate(participate)
            .awardScale(awardScale)
            .startDate(startDate)
            .endDate(endDate)
            .homepageUrl(homepageUrl)
            .activityBenefit(activityBenefit)
            .bonusBenefit(bonusBenefit)
            .description(description)
            .imageUrl(imageUrl)
            .status(status).build();

        competitionRepository.save(competition);
    }

    private List<Integer> initPage(WebDriver chromeDriver, SearchCrawling searchCrawling) {
        List<Integer> pages = new ArrayList<>();
        String baseUrl = choiceEntity(searchCrawling);
        chromeDriver.get(baseUrl.concat("1"));
        List<WebElement> elements = chromeDriver.findElements(By.className("MuiButton-label"));
        for (int i = 1; i < elements.size() - 1; i++) {
            pages.add(Integer.parseInt(elements.get(i).getText()));
        }
        return pages;
    }

    private String choiceEntity(SearchCrawling searchCrawling) {
        if (searchCrawling.equals(SearchCrawling.ACTIVITY)) {
            return activityBaseUrl;
        }
        return competitionBaseUrl;
    }

    private LocalDate dateFormatter(String startDate) {
        String[] tempStartDate = startDate.split("\\.");
        if (tempStartDate.length < 3){
            return LocalDate.now().plusDays(90);
        }
        StringBuilder temp = new StringBuilder("20");
        for (String s : tempStartDate) {
            if (s.length() == 1) {
                temp.append("0").append(s).append("-");
            } else {
                temp.append(s).append("-");
            }
        }
        return LocalDate.parse(temp.substring(0, temp.length() - 1));
    }


    private RecruitmentStatus setStatus(LocalDate startDate) {
        LocalDate now = LocalDate.now();
        if (startDate.isEqual(now) || startDate.isBefore(now)) {
            return RecruitmentStatus.DURING;
        }
        return RecruitmentStatus.BEFORE;
    }

    private List<String> getRecruitingTitles(List<Integer> pages, WebDriver chromeDriver,
        SearchCrawling searchCrawling)
        throws InterruptedException {
        List<String> recruitingTitles = new ArrayList<>();
        String baseUrl = choiceEntity(searchCrawling);
        for (Integer page : pages) {
            chromeDriver.get(baseUrl.concat(page.toString()));

            Thread.sleep(1000);

            recruitingTitles.addAll(chromeDriver.findElements(By.className("activity-title"))
                .stream().map(WebElement::getText).toList());
        }
        return recruitingTitles;
    }

}