# 2026-05-29-infinite-tool-call-in-m8
there's an oddity with infinite tool calling in Spring AI M8 + memory 

you need ollama to make this run. 

im using some big models but you might be able to get away with less. 

them visit:

http://127.0.0.1:8080/josh/ask?question=fantastic.%20when%20can%20I%20pick%20up%20Prancer%20(dog%20ID%2045)%20from%20the%20Lisbon%20Pooch%20Palace%20location%3F&continue

it calls the tool over and over. if u change the DB to use PostgreSQL or somethign youc an see that its adding many blank rows in the chat memory table too

---

Under the hood, the Arconia framework will automatically spin up an [OpenLit](https://docs.arconia.io/arconia/latest/dev-services/openlit/) AI observability platform and a [PostgreSQL](https://docs.arconia.io/arconia/latest/dev-services/postgresql/) database using Testcontainers. It will also spin up an [Ollama](https://docs.arconia.io/arconia/latest/dev-services/ollama/) service, in case you don't have one running locally (see [Arconia Dev Services](https://docs.arconia.io/arconia/latest/dev-services/) for more information).

The application will be accessible at http://localhost:8080.

The application logs will show you the URL where you can access the OpenLit AI observability platform.

```logs
...OpenLit UI: http://localhost:<port>
```

By default, traces are exported via OTLP using the HTTP/Protobuf format.
