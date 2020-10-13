package cim.model.enums;

public enum Method {
    GET {
        @Override
        public String toString() {
            return "get";
        }
    },
    POST {
        @Override
        public String toString() {
            return "post";
        }
    },
    DELETE {
        @Override
        public String toString() {
            return "delete";
        }
    },
    PUT {
        @Override
        public String toString() {
            return "put";
        }
    },
    PATCH {
        @Override
        public String toString() {
            return "patch";
        }
    }
    

}
