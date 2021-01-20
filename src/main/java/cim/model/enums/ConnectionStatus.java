package cim.model.enums;

public enum ConnectionStatus {
    CONNECTED {
        @Override
        public String toString() {
            return "Connected";
        }
    },
    DISCONNECTED {
        @Override
        public String toString() {
            return "Disconnected";
        }
    },
    CERTIFICATE_NOT_FOUND {
        @Override
        public String toString() {
            return "Certificate not found";
        }
    },
    INCORRECT_CERTIFICATE_PASSWORD {
        @Override
        public String toString() {
            return "Incorrect certificate password";
        }
    },
    INCORRECT_XMPP_USER_CREDENTIALS {
        @Override
        public String toString() {
            return "Incorrect xmpp user credentials";
        }
    },
    ERROR_CONNECTING_WITH_OPENFIRE {
        @Override
        public String toString() {
            return "Error connecting with openfire";
        }
    },
    ALREADY_CONNECTED {
        @Override
        public String toString() {
            return "Already connected";
        }
    },
    BAD_CERTIFICATE {
        @Override
        public String toString() {
            return "Bad certificate";
        }
    },
  }
