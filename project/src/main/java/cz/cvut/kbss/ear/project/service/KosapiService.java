package cz.cvut.kbss.ear.project.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import cz.cvut.kbss.ear.project.exception.KosapiException;
import cz.cvut.kbss.ear.project.kosapi.entities.*;
import cz.cvut.kbss.ear.project.kosapi.links.ParallelLink;
import cz.cvut.kbss.ear.project.kosapi.links.TeacherLink;
import cz.cvut.kbss.ear.project.kosapi.wrappers.Entry;
import cz.cvut.kbss.ear.project.kosapi.wrappers.WrappedEntries;
import cz.cvut.kbss.ear.project.kosapi.oauth2.TokenManager;
import cz.cvut.kbss.ear.project.model.enums.CourseCompletionType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.Charset;
import java.util.*;

@Service
public class KosapiService {

    public static final String AUTHORIZATION = "Authorization";

    @Value("${resource.server.url}")
    private String resourceServerURL;

    @Autowired
    private TokenManager tokenManager;

    @Autowired
    private RestTemplate restTemplate;

    private String token;

    public KosapiService(TokenManager tokenManager, RestTemplate restTemplate) {
        this.tokenManager = tokenManager;
        this.restTemplate = restTemplate;
        this.token = tokenManager.getAccessToken();
        restTemplate.getMessageConverters()
                .add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
    }

    public KosCourse getCourse(String courseCode){
        return getCourseInSemester(courseCode, null);
    }

    public KosCourse getCourseInSemester(String courseCode, String semesterCode) {
        HttpEntity<Void> request = getHttpRequestEntity();
        String courseUrl = "/courses/" + courseCode;
        if (semesterCode != null) courseUrl += "?sem=" + semesterCode;
        String response = restTemplate.exchange(resourceServerURL + courseUrl, HttpMethod.GET, request, String.class).getBody();
        ObjectMapper xmlMapper = new XmlMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        try {
            Entry<KosCourse> atomEntry = xmlMapper.readValue(response, new TypeReference<Entry<KosCourse>>() {});
            KosCourse kosCourse = atomEntry.getContent();
            return kosCourse;
        } catch (JsonProcessingException e) {
            throw new KosapiException("Failed to parse course: " + courseCode
                    + "\nError message:" + e.getMessage());
        }
    }

    private CourseCompletionType convertCompletionType(String kosapiCompletionType){
        switch(kosapiCompletionType){
            case "CLFD_CREDIT":
                return CourseCompletionType.CLFD_CREDIT;
            case "CREDIT_EXAM":
                return CourseCompletionType.CREDIT_EXAM;
            case "CREDIT":
                return CourseCompletionType.CREDIT;
            case "DEFENCE":
                return CourseCompletionType.DEFENCE;
            case "EXAM":
                return CourseCompletionType.EXAM;
            default:
                return CourseCompletionType.UNDEFINED;

        }
    }

    public List<KosCourse> getAllCoursesInSemester(String semesterCode){
        try {
            ArrayList<KosCourse> kosCourses = getAllCourseEntriesInSemester(semesterCode);
            return kosCourses;
        } catch (JsonProcessingException e) {
            throw new KosapiException("Failed to parse courses from semester: " + semesterCode
                    + "\nError message:" + e.getMessage());
        }
    }

    private ArrayList<KosCourse> getAllCourseEntriesInSemester(String semesterCode) throws JsonProcessingException {
        int offset = 0;
        ArrayList<KosCourse> result = new ArrayList<>();

        while (true){
            HttpEntity<Void> request = getHttpRequestEntity();
            String courseUrl = "/courses?sem=" + semesterCode + "&limit=1000&offset=" + offset;
            String response = restTemplate.exchange(resourceServerURL + courseUrl, HttpMethod.GET, request, String.class).getBody();

            ObjectMapper xmlMapper = new XmlMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            WrappedEntries<KosCourse> entries = xmlMapper.readValue(response, new TypeReference<WrappedEntries<KosCourse>>() {});
            if (entries.getContentList().size() != 0){
                result.addAll(entries.getContentList());
                offset += 1000;
            }

            else{
                break;
            }
        }

        return result;
    }

    public List<KosTeacher> getTeachersInCourse(KosCourse kosCourse){
        KosCourseInstance instance = kosCourse.getInstance();
        TreeSet<TeacherLink> teachersLinks = new TreeSet<TeacherLink>(Arrays.asList(instance.getInstructors()));
        teachersLinks.addAll(Arrays.asList(instance.getLecturers()));
        ArrayList<KosTeacher> kosTeachers = new ArrayList<>();

        for (TeacherLink teacherLink : teachersLinks){
            try {
                kosTeachers.add(getKosTeacherFromTeacherLink(teacherLink));
            } catch (JsonProcessingException e) {
                e.printStackTrace(); // TODO logging later
            }
        }

        return kosTeachers;
    }


    private KosTeacher getKosTeacherFromTeacherLink(TeacherLink teacherLink) throws JsonProcessingException {
        HttpEntity<Void> request = getHttpRequestEntity();
        String response = restTemplate.exchange(resourceServerURL + "/" + teacherLink.getUrl(), HttpMethod.GET, request, String.class).getBody();
        ObjectMapper xmlMapper = new XmlMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        Entry<KosTeacher> atomEntry = xmlMapper.readValue(response, new TypeReference<Entry<KosTeacher>>() {
        });
        return atomEntry.getContent();
    }

    public List<KosParallel> getParallelsInCourse(String courseCode, String semesterCode){
        HttpEntity<Void> request = getHttpRequestEntity();
        String parallelUrl = "/courses/" + courseCode + "/parallels" + "?sem=" + semesterCode;
        String response = restTemplate.exchange(resourceServerURL + parallelUrl, HttpMethod.GET, request, String.class).getBody();
        ObjectMapper xmlMapper = new XmlMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            WrappedEntries<KosParallel> wrappedEntries = xmlMapper.readValue(response, new TypeReference<WrappedEntries<KosParallel>>() {});
            return pairParallelsWithLinks(wrappedEntries);

        } catch (JsonProcessingException e) {
            throw new KosapiException("Failed to parse parallels from course: " + courseCode
                    + " semester:" + semesterCode
                    + "\nError message:" + e.getMessage());
        }
    }

    private List<KosParallel> pairParallelsWithLinks(WrappedEntries<KosParallel> wrappedEntries){
        List<Entry<KosParallel>> entryList = wrappedEntries.unwrap();
        ArrayList<KosParallel> parallels = new ArrayList<>();

        for (Entry<KosParallel> entry : entryList){
            ParallelLink parallelLink = new ParallelLink();
            parallelLink.setUrl(entry.getLink().getUrl());
            KosParallel parallel = entry.getContent();
            parallel.setLink(parallelLink);
            parallels.add(parallel);
        }

        return parallels;
    }

    public List<KosTeacher> getTeachersInParallel(KosParallel kosParallel){
        ArrayList<KosTeacher> kosTeachers = new ArrayList<>();
        if (kosParallel.getTeacherlinks() == null) return new ArrayList<>();
        for (TeacherLink teacherLink : kosParallel.getTeacherlinks()){
            try {
                kosTeachers.add(getKosTeacherFromTeacherLink(teacherLink));
            } catch (JsonProcessingException e) {
                e.printStackTrace(); // TODO logging
            }
        }

        return kosTeachers;
    }

    public List<KosStudent> getStudentsInParallel(KosParallel kosParallel){
        HttpEntity<Void> request = getHttpRequestEntity();
        String courseUrl = "/" + kosParallel.getLink().getUrl() + "students" + "?limit=1000";
        String response = restTemplate.exchange(resourceServerURL + courseUrl, HttpMethod.GET, request, String.class).getBody();
        ObjectMapper xmlMapper = new XmlMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            WrappedEntries<KosStudent> wrappedEntries = xmlMapper.readValue(response, new TypeReference<WrappedEntries<KosStudent>>() {});
            return wrappedEntries.getContentList();
        } catch (JsonProcessingException e) {
            throw new KosapiException("Failed to parse students from parallel: " + kosParallel.getLink().getUrl()
                    + " code: " + kosParallel.getCode()
                    + "\nError message:" + e.getMessage());
        }
    }

    private HttpEntity<Void> getHttpRequestEntity(){
        HttpHeaders headers = new HttpHeaders();
        headers.add(AUTHORIZATION, "Bearer " + token);
        return new HttpEntity<>(headers);
    }

    public List<KosStudent> getStudentsInCourse(KosCourse kosCourse){
        HttpEntity<Void> request = getHttpRequestEntity();
        String courseUrl = "/courses/" + kosCourse.getCode() + "/students" + "?sem="+ kosCourse.getInstance().getSemesterCode() + "&limit=1000";
        String response = restTemplate.exchange(resourceServerURL + courseUrl, HttpMethod.GET, request, String.class).getBody();
        ObjectMapper xmlMapper = new XmlMapper().configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        try {
            WrappedEntries<KosStudent> wrappedEntries = xmlMapper.readValue(response, new TypeReference<WrappedEntries<KosStudent>>() {});
            return wrappedEntries.getContentList();
        } catch (JsonProcessingException e) {
            throw new KosapiException("Failed to parse students from course: " + kosCourse.getCode()
                    + "\nError message:" + e.getMessage());
        }
    }
}
