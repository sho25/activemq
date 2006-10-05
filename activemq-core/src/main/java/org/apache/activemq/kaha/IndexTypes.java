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
name|kaha
package|;
end_package

begin_comment
comment|/**  * Types of Indexes used by the Store  *   * @version $Revision: 1.2 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|IndexTypes
block|{
comment|/**      * use in memory indexes      */
specifier|public
specifier|final
specifier|static
name|String
name|IN_MEMORY_INDEX
init|=
literal|"InMemoryIndex"
decl_stmt|;
comment|/**      * use disk-based indexes      */
specifier|public
specifier|final
specifier|static
name|String
name|DISK_INDEX
init|=
literal|"DiskIndex"
decl_stmt|;
block|}
end_interface

end_unit

