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
name|broker
operator|.
name|transport
operator|.
name|protocol
package|;
end_package

begin_import
import|import
name|java
operator|.
name|nio
operator|.
name|charset
operator|.
name|StandardCharsets
import|;
end_import

begin_comment
comment|/**  *  *  */
end_comment

begin_class
specifier|public
class|class
name|StompProtocolVerifier
implements|implements
name|ProtocolVerifier
block|{
comment|/* (non-Javadoc)      * @see org.apache.activemq.broker.transport.protocol.ProtocolVerifier#isProtocol(byte[])      */
annotation|@
name|Override
specifier|public
name|boolean
name|isProtocol
parameter_list|(
name|byte
index|[]
name|value
parameter_list|)
block|{
name|String
name|frameStart
init|=
operator|new
name|String
argument_list|(
name|value
argument_list|,
name|StandardCharsets
operator|.
name|US_ASCII
argument_list|)
decl_stmt|;
return|return
name|frameStart
operator|.
name|startsWith
argument_list|(
literal|"CONNECT"
argument_list|)
operator|||
name|frameStart
operator|.
name|startsWith
argument_list|(
literal|"STOMP"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

