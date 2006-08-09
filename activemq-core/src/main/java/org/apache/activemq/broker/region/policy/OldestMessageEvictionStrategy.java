begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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

begin_import
import|import
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
name|MessageReference
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedList
import|;
end_import

begin_comment
comment|/**  * An eviction strategy which evicts the oldest message first (which is the  * default).  *   * @org.apache.xbean.XBean  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|OldestMessageEvictionStrategy
extends|extends
name|MessageEvictionStrategySupport
block|{
specifier|public
name|MessageReference
index|[]
name|evictMessages
parameter_list|(
name|LinkedList
name|messages
parameter_list|)
block|{
return|return
operator|new
name|MessageReference
index|[]
block|{
operator|(
name|MessageReference
operator|)
name|messages
operator|.
name|removeFirst
argument_list|()
block|}
return|;
block|}
block|}
end_class

end_unit

