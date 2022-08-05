### Compile and Install 

```bash
$ mvn clean package dockerfile:build
$ docker-compose -f docker-compose.yml up
```

### issues

* line items still cannot work
* session expired time setting, consider write it into DB
