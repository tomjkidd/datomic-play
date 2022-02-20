.PHONY: check-env test

DATOMIC_ACCESS_KEY?=myaccesskey
DATOMIC_SECRET?=mysecret
DATOMIC_DB_NAME?=hello

check-env:
ifndef DATOMIC_INSTALL_DIR
	$(error DATOMIC_INSTALL_DIR is undefined, provide it so that the datomic install location can be configured.)
endif

test:
	clj -X:test

run-peer: check-env
	$(DATOMIC_INSTALL_DIR)/bin/run -m datomic.peer-server -h localhost -p 8998 -a $(DATOMIC_ACCESS_KEY),$(DATOMIC_SECRET) -d hello,datomic:mem://hello
