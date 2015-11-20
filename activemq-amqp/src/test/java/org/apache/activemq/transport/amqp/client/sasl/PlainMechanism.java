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
name|amqp
operator|.
name|client
operator|.
name|sasl
package|;
end_package

begin_comment
comment|/**  * Implements the SASL PLAIN authentication Mechanism.  *  * User name and Password values are sent without being encrypted.  */
end_comment

begin_class
specifier|public
class|class
name|PlainMechanism
extends|extends
name|AbstractMechanism
block|{
specifier|public
specifier|static
specifier|final
name|String
name|MECH_NAME
init|=
literal|"PLAIN"
decl_stmt|;
annotation|@
name|Override
specifier|public
name|int
name|getPriority
parameter_list|()
block|{
return|return
name|PRIORITY
operator|.
name|MEDIUM
operator|.
name|getValue
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|MECH_NAME
return|;
block|}
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|getInitialResponse
parameter_list|()
block|{
name|String
name|authzid
init|=
name|getAuthzid
argument_list|()
decl_stmt|;
name|String
name|username
init|=
name|getUsername
argument_list|()
decl_stmt|;
name|String
name|password
init|=
name|getPassword
argument_list|()
decl_stmt|;
if|if
condition|(
name|authzid
operator|==
literal|null
condition|)
block|{
name|authzid
operator|=
literal|""
expr_stmt|;
block|}
if|if
condition|(
name|username
operator|==
literal|null
condition|)
block|{
name|username
operator|=
literal|""
expr_stmt|;
block|}
if|if
condition|(
name|password
operator|==
literal|null
condition|)
block|{
name|password
operator|=
literal|""
expr_stmt|;
block|}
name|byte
index|[]
name|authzidBytes
init|=
name|authzid
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|byte
index|[]
name|usernameBytes
init|=
name|username
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|byte
index|[]
name|passwordBytes
init|=
name|password
operator|.
name|getBytes
argument_list|()
decl_stmt|;
name|byte
index|[]
name|data
init|=
operator|new
name|byte
index|[
name|authzidBytes
operator|.
name|length
operator|+
literal|1
operator|+
name|usernameBytes
operator|.
name|length
operator|+
literal|1
operator|+
name|passwordBytes
operator|.
name|length
index|]
decl_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|authzidBytes
argument_list|,
literal|0
argument_list|,
name|data
argument_list|,
literal|0
argument_list|,
name|authzidBytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|usernameBytes
argument_list|,
literal|0
argument_list|,
name|data
argument_list|,
literal|1
operator|+
name|authzidBytes
operator|.
name|length
argument_list|,
name|usernameBytes
operator|.
name|length
argument_list|)
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|passwordBytes
argument_list|,
literal|0
argument_list|,
name|data
argument_list|,
literal|2
operator|+
name|authzidBytes
operator|.
name|length
operator|+
name|usernameBytes
operator|.
name|length
argument_list|,
name|passwordBytes
operator|.
name|length
argument_list|)
expr_stmt|;
return|return
name|data
return|;
block|}
annotation|@
name|Override
specifier|public
name|byte
index|[]
name|getChallengeResponse
parameter_list|(
name|byte
index|[]
name|challenge
parameter_list|)
block|{
return|return
name|EMPTY
return|;
block|}
block|}
end_class

end_unit

