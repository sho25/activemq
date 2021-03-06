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
name|region
operator|.
name|policy
package|;
end_package

begin_comment
comment|/**  * A useful base class for implementation inheritence.  *   *   */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|MessageEvictionStrategySupport
implements|implements
name|MessageEvictionStrategy
block|{
specifier|private
name|int
name|evictExpiredMessagesHighWatermark
init|=
literal|1000
decl_stmt|;
specifier|public
name|int
name|getEvictExpiredMessagesHighWatermark
parameter_list|()
block|{
return|return
name|evictExpiredMessagesHighWatermark
return|;
block|}
comment|/**      * Sets the high water mark on which we will eagerly evict expired messages from RAM      */
specifier|public
name|void
name|setEvictExpiredMessagesHighWatermark
parameter_list|(
name|int
name|evictExpiredMessagesHighWaterMark
parameter_list|)
block|{
name|this
operator|.
name|evictExpiredMessagesHighWatermark
operator|=
name|evictExpiredMessagesHighWaterMark
expr_stmt|;
block|}
block|}
end_class

end_unit

