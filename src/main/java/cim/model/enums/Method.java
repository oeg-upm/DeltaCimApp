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
    }

}
