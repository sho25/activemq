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
name|memory
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
comment|/**  * A simple least-recently-used cache of a fixed size.  *   * @version $Revision:$  */
end_comment

begin_class
specifier|public
class|class
name|LRUMap
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
literal|9179676638408888162L
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|float
name|DEFAULT_LOAD_FACTOR
init|=
operator|(
name|float
operator|)
literal|0.75
decl_stmt|;
specifier|protected
specifier|static
specifier|final
name|int
name|DEFAULT_INITIAL_CAPACITY
init|=
literal|5000
decl_stmt|;
specifier|private
name|int
name|maximumSize
decl_stmt|;
specifier|public
name|LRUMap
parameter_list|(
name|int
name|maximumSize
parameter_list|)
block|{
name|this
argument_list|(
name|DEFAULT_INITIAL_CAPACITY
argument_list|,
name|DEFAULT_LOAD_FACTOR
argument_list|,
literal|true
argument_list|,
name|maximumSize
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LRUMap
parameter_list|(
name|int
name|maximumSize
parameter_list|,
name|boolean
name|accessOrder
parameter_list|)
block|{
name|this
argument_list|(
name|DEFAULT_INITIAL_CAPACITY
argument_list|,
name|DEFAULT_LOAD_FACTOR
argument_list|,
name|accessOrder
argument_list|,
name|maximumSize
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LRUMap
parameter_list|(
name|int
name|initialCapacity
parameter_list|,
name|float
name|loadFactor
parameter_list|,
name|boolean
name|accessOrder
parameter_list|,
name|int
name|maximumSize
parameter_list|)
block|{
name|super
argument_list|(
name|initialCapacity
argument_list|,
name|loadFactor
argument_list|,
name|accessOrder
argument_list|)
expr_stmt|;
name|this
operator|.
name|maximumSize
operator|=
name|maximumSize
expr_stmt|;
block|}
specifier|protected
name|boolean
name|removeEldestEntry
parameter_list|(
name|Map
operator|.
name|Entry
name|eldest
parameter_list|)
block|{
return|return
name|size
argument_list|()
operator|>
name|maximumSize
return|;
block|}
block|}
end_class

end_unit

