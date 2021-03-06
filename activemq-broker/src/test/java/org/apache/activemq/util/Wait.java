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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
import|;
end_import

begin_class
specifier|public
class|class
name|Wait
block|{
specifier|public
specifier|static
specifier|final
name|long
name|MAX_WAIT_MILLIS
init|=
literal|30
operator|*
literal|1000
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|long
name|SLEEP_MILLIS
init|=
literal|1000
decl_stmt|;
specifier|public
interface|interface
name|Condition
block|{
name|boolean
name|isSatisified
parameter_list|()
throws|throws
name|Exception
function_decl|;
block|}
specifier|public
specifier|static
name|boolean
name|waitFor
parameter_list|(
name|Condition
name|condition
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|waitFor
argument_list|(
name|condition
argument_list|,
name|MAX_WAIT_MILLIS
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|waitFor
parameter_list|(
specifier|final
name|Condition
name|condition
parameter_list|,
specifier|final
name|long
name|duration
parameter_list|)
throws|throws
name|Exception
block|{
return|return
name|waitFor
argument_list|(
name|condition
argument_list|,
name|duration
argument_list|,
name|SLEEP_MILLIS
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|boolean
name|waitFor
parameter_list|(
specifier|final
name|Condition
name|condition
parameter_list|,
specifier|final
name|long
name|duration
parameter_list|,
specifier|final
name|long
name|sleepMillis
parameter_list|)
throws|throws
name|Exception
block|{
specifier|final
name|long
name|expiry
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|duration
decl_stmt|;
name|boolean
name|conditionSatisified
init|=
name|condition
operator|.
name|isSatisified
argument_list|()
decl_stmt|;
while|while
condition|(
operator|!
name|conditionSatisified
operator|&&
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|<
name|expiry
condition|)
block|{
name|TimeUnit
operator|.
name|MILLISECONDS
operator|.
name|sleep
argument_list|(
name|sleepMillis
argument_list|)
expr_stmt|;
name|conditionSatisified
operator|=
name|condition
operator|.
name|isSatisified
argument_list|()
expr_stmt|;
block|}
return|return
name|conditionSatisified
return|;
block|}
block|}
end_class

end_unit

