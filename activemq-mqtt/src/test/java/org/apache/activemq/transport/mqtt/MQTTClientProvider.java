begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|transport
operator|.
name|mqtt
package|;
end_package

begin_interface
specifier|public
interface|interface
name|MQTTClientProvider
block|{
name|void
name|connect
parameter_list|(
name|String
name|host
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|void
name|disconnect
parameter_list|()
throws|throws
name|Exception
function_decl|;
specifier|public
name|void
name|publish
parameter_list|(
name|String
name|topic
parameter_list|,
name|byte
index|[]
name|payload
parameter_list|,
name|int
name|qos
parameter_list|,
name|boolean
name|retained
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|void
name|publish
parameter_list|(
name|String
name|topic
parameter_list|,
name|byte
index|[]
name|payload
parameter_list|,
name|int
name|qos
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|void
name|subscribe
parameter_list|(
name|String
name|topic
parameter_list|,
name|int
name|qos
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|void
name|unsubscribe
parameter_list|(
name|String
name|topic
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|byte
index|[]
name|receive
parameter_list|(
name|int
name|timeout
parameter_list|)
throws|throws
name|Exception
function_decl|;
name|void
name|setSslContext
parameter_list|(
name|javax
operator|.
name|net
operator|.
name|ssl
operator|.
name|SSLContext
name|sslContext
parameter_list|)
function_decl|;
name|void
name|setWillMessage
parameter_list|(
name|String
name|string
parameter_list|)
function_decl|;
name|void
name|setWillTopic
parameter_list|(
name|String
name|topic
parameter_list|)
function_decl|;
name|void
name|setClientId
parameter_list|(
name|String
name|clientId
parameter_list|)
function_decl|;
name|void
name|kill
parameter_list|()
throws|throws
name|Exception
function_decl|;
name|void
name|setKeepAlive
parameter_list|(
name|int
name|keepAlive
parameter_list|)
throws|throws
name|Exception
function_decl|;
block|}
end_interface

end_unit

