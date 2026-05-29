# 2026-05-29-infinite-tool-call-in-m8
there's an oddity with infinite tool calling in Spring AI M8 + memory 

you need ollama to make this run. 

im using some big models but you might be able to get away with less. 

them visit:

http://127.0.0.1:8080/josh/ask?question=fantastic.%20when%20can%20I%20pick%20up%20Prancer%20(dog%20ID%2045)%20from%20the%20Lisbon%20Pooch%20Palace%20location%3F&continue

it calls the tool over and over. if u change the DB to use PostgreSQL or somethign youc an see that its adding many blank rows in the chat memory table too
