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
name|util
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|LinkedHashMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Map
import|;
end_import

begin_comment
comment|/**  * A Simple LRU Cache  *   * @version $Revision$  */
end_comment

begin_class
specifier|public
class|class
name|LRUCache
extends|extends
name|LinkedHashMap
block|{
specifier|private
specifier|static
specifier|final
name|long
name|serialVersionUID
init|=
operator|-
literal|342098639681884413L
decl_stmt|;
specifier|protected
name|int
name|maxCacheSize
init|=
literal|10000
decl_stmt|;
comment|/**      * Constructs LRU Cache      *       */
specifier|public
name|LRUCache
parameter_list|()
block|{
name|super
argument_list|(
literal|1000
argument_list|,
literal|0.75f
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return Returns the maxCacheSize.      */
specifier|public
name|int
name|getMaxCacheSize
parameter_list|()
block|{
return|return
name|maxCacheSize
return|;
block|}
comment|/**      * @param maxCacheSize      *            The maxCacheSize to set.      */
specifier|public
name|void
name|setMaxCacheSize
parameter_list|(
name|int
name|maxCacheSize
parameter_list|)
block|{
name|this
operator|.
name|maxCacheSize
operator|=
name|maxCacheSize
expr_stmt|;
block|}
specifier|protected
name|boolean
name|removeEldestEntry
parameter_list|(
name|Map
operator|.
name|Entry
name|entry
parameter_list|)
block|{
return|return
name|size
argument_list|()
operator|>
name|maxCacheSize
return|;
block|}
block|}
end_class

end_unit

