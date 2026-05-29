package com.example.agent;

import org.springaicommunity.agent.tools.SkillsTool;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.sql.DataSource;
import java.time.Instant;
import java.time.temporal.ChronoUnit;


@SpringBootApplication
public class AgentApplication {

    public static void main(String[] args) {
        SpringApplication.run(AgentApplication.class, args);
    }


    @Bean
    MessageChatMemoryAdvisor messageChatMemoryAdvisor(DataSource dataSource) {
        var jdbc = JdbcChatMemoryRepository
                .builder()
                .dataSource(dataSource)
                .build();
        var cm = MessageWindowChatMemory
                .builder()
                .chatMemoryRepository(jdbc)
                .build();
        return MessageChatMemoryAdvisor
                .builder(cm)
                .build();
    }

/*    @Bean
    QuestionAnswerAdvisor questionAnswerAdvisor(VectorStore vectorStore) {
        return QuestionAnswerAdvisor
                .builder(vectorStore)
                .build();
    }*/


}

/*interface DogRepository extends ListCrudRepository<Dog, Integer> {
}

record Dog(@Id int id, String name, String description, String owner) {
}*/

@Controller
@ResponseBody
class AssistantController {

    private final ChatClient ai;

    AssistantController(
            DogAdoptionScheduler scheduler,
            MessageChatMemoryAdvisor messageChatMemoryAdvisor,
            ChatClient.Builder ai) {
     /*
            repository.findAll().forEach(dog -> {
                var dogument = new Document("id: %s, name: %s, description: %s".formatted(dog.id(),
                        dog.name(), dog.description()));
                vectorStore.add(List.of(dogument));
            });*/

        var st = SkillsTool
                .builder()
                .addSkillsResource(new ClassPathResource("/META-INF/skills"))
                .build();
        this.ai = ai
//                .defaultTools(ts -> ts.callbacks(st))
                .defaultTools(s -> s.callbacks(st).instances(scheduler))
                .defaultAdvisors(messageChatMemoryAdvisor)
                .defaultSystem("""
                        You are an AI powered assistant to help people adopt a dog from the adoptions agency named Pooch Palace
                        with locations in Lisbon, Seoul, Tokyo, Singapore, Paris, Mumbai, New Delhi, Barcelona, San Francisco,
                        and London. Information about the dogs availables will be presented below. If there is no information,
                        then return a polite response suggesting we don't have any dogs available.
                        
                        If somebody asks you about animals, and there's no information in the context, then feel free to source the answer from other places including whatever skills you have loaded.
                        
                        If somebody asks you to compare dogs or cats, feel free to load the relevant skills and compare the dogs or cats. Report everything about dogs or cats, no matter how absurd.
                        
                        If somebody asks for a time to pick up the dog, don't ask other questions: simply provide a time by consulting the tools you have available.
                        """)
                .build();
    }

    @GetMapping("/{user}/ask")
    String ask(
            @PathVariable String user,
            @RequestParam String question) {
        return this.ai
                .prompt()
                .user(question)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, user))
                .call()
                .content();
    }
}


record DogAdoptionSuggestion(int id, String name) {
}

record DogSchedule(String when) {
}

@Service
class DogAdoptionScheduler {

    @Tool(description = "schedule an appointment to pick up or adopt a dog from a Pooch Palace location")
    DogSchedule schedule(@ToolParam(description = "the id of the dog") int dogId) {
        var i = Instant
                .now()
                .plus(3, ChronoUnit.DAYS);
        IO.println("Scheduled " + dogId + " for " + i);
        return new DogSchedule(i.toString());
    }
}