package com.telemune.model;

public class MessageData {
        private  String refId;
		private String msisdn;
		private String status;
		private String deliveryTime;

        public MessageData(String refId, String msisdn, String status, String deliveryTime) {
            this.refId = refId;
            this.msisdn = msisdn;
            this.status = status;
            this.deliveryTime = deliveryTime;
        }

		public String getRefId() {
			return refId;
		}

		public void setRefId(String refId) {
			this.refId = refId;
		}

		public String getMsisdn() {
			return msisdn;
		}

		public void setMsisdn(String msisdn) {
			this.msisdn = msisdn;
		}

		public String getStatus() {
			return status;
		}

		public void setStatus(String status) {
			this.status = status;
		}

		public String getDeliveryTime() {
			return deliveryTime;
		}

		public void setDeliveryTime(String deliveryTime) {
			this.deliveryTime = deliveryTime;
		}

		@Override
		public String toString() {
			return "MessageData [refId=" + refId + ", msisdn=" + msisdn + ", status=" + status + ", deliveryTime="
					+ deliveryTime + "]";
		}
    


}
