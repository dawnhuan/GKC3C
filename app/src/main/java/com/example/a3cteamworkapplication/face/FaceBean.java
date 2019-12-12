package com.example.a3cteamworkapplication.face;

import java.util.List;

/**
 * desc:
 */
public class FaceBean {


    /**
     * error_code : 0
     * error_msg : SUCCESS
     * log_id : 9475201001847
     * timestamp : 1575184824
     * cached : 0
     * result : {"face_num":1,"face_list":[{"face_token":"b8e71d36e5262fc5d6813b4bedaaa9f3","location":{"left":514.35,"top":1290.05,"width":751,"height":729,"rotation":-3},"face_probability":1,"angle":{"yaw":0.83,"pitch":20.92,"roll":-5.26},"expression":{"type":"none","probability":0.99}}]}
     */

    private int error_code;
    private String error_msg;
    private long log_id;
    private int timestamp;
    private int cached;
    private ResultBean result;

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public long getLog_id() {
        return log_id;
    }

    public void setLog_id(long log_id) {
        this.log_id = log_id;
    }

    public int getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(int timestamp) {
        this.timestamp = timestamp;
    }

    public int getCached() {
        return cached;
    }

    public void setCached(int cached) {
        this.cached = cached;
    }

    public ResultBean getResult() {
        return result;
    }

    public void setResult(ResultBean result) {
        this.result = result;
    }

    public static class ResultBean {
        /**
         * face_num : 1
         * face_list : [{"face_token":"b8e71d36e5262fc5d6813b4bedaaa9f3","location":{"left":514.35,"top":1290.05,"width":751,"height":729,"rotation":-3},"face_probability":1,"angle":{"yaw":0.83,"pitch":20.92,"roll":-5.26},"expression":{"type":"none","probability":0.99}}]
         */

        private int face_num;
        private List<FaceListBean> face_list;

        public int getFace_num() {
            return face_num;
        }

        public void setFace_num(int face_num) {
            this.face_num = face_num;
        }

        public List<FaceListBean> getFace_list() {
            return face_list;
        }

        public void setFace_list(List<FaceListBean> face_list) {
            this.face_list = face_list;
        }

        public static class FaceListBean {
            /**
             * face_token : b8e71d36e5262fc5d6813b4bedaaa9f3
             * location : {"left":514.35,"top":1290.05,"width":751,"height":729,"rotation":-3}
             * face_probability : 1
             * angle : {"yaw":0.83,"pitch":20.92,"roll":-5.26}
             * expression : {"type":"none","probability":0.99}
             */

            private String face_token;
            private LocationBean location;
            private double face_probability;
            private AngleBean angle;
            private ExpressionBean expression;

            public String getFace_token() {
                return face_token;
            }

            public void setFace_token(String face_token) {
                this.face_token = face_token;
            }

            public LocationBean getLocation() {
                return location;
            }

            public void setLocation(LocationBean location) {
                this.location = location;
            }

            public double getFace_probability() {
                return face_probability;
            }

            public void setFace_probability(double face_probability) {
                this.face_probability = face_probability;
            }

            public AngleBean getAngle() {
                return angle;
            }

            public void setAngle(AngleBean angle) {
                this.angle = angle;
            }

            public ExpressionBean getExpression() {
                return expression;
            }

            public void setExpression(ExpressionBean expression) {
                this.expression = expression;
            }

            public static class LocationBean {
                /**
                 * left : 514.35
                 * top : 1290.05
                 * width : 751
                 * height : 729
                 * rotation : -3
                 */

                private double left;
                private double top;
                private int width;
                private int height;
                private int rotation;

                public double getLeft() {
                    return left;
                }

                public void setLeft(double left) {
                    this.left = left;
                }

                public double getTop() {
                    return top;
                }

                public void setTop(double top) {
                    this.top = top;
                }

                public int getWidth() {
                    return width;
                }

                public void setWidth(int width) {
                    this.width = width;
                }

                public int getHeight() {
                    return height;
                }

                public void setHeight(int height) {
                    this.height = height;
                }

                public int getRotation() {
                    return rotation;
                }

                public void setRotation(int rotation) {
                    this.rotation = rotation;
                }
            }

            public static class AngleBean {
                /**
                 * yaw : 0.83
                 * pitch : 20.92
                 * roll : -5.26
                 */

                private double yaw;
                private double pitch;
                private double roll;

                public double getYaw() {
                    return yaw;
                }

                public void setYaw(double yaw) {
                    this.yaw = yaw;
                }

                public double getPitch() {
                    return pitch;
                }

                public void setPitch(double pitch) {
                    this.pitch = pitch;
                }

                public double getRoll() {
                    return roll;
                }

                public void setRoll(double roll) {
                    this.roll = roll;
                }
            }

            public static class ExpressionBean {
                /**
                 * type : none
                 * probability : 0.99
                 */

                private String type;
                private double probability;

                public String getType() {
                    return type;
                }

                public void setType(String type) {
                    this.type = type;
                }

                public double getProbability() {
                    return probability;
                }

                public void setProbability(double probability) {
                    this.probability = probability;
                }
            }
        }
    }
}
