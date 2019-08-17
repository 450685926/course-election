package com.server.edu.election.service;

        import java.util.List;
        import java.util.Map;

public interface CultureService {
    Map<String,String> findStudentFirstLanguageCode(List<String> studentIds);


}
