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
name|leveldb
operator|.
name|replicated
operator|.
name|dto
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAccessorType
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlAttribute
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|xml
operator|.
name|bind
operator|.
name|annotation
operator|.
name|XmlRootElement
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_comment
comment|/**  * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_class
annotation|@
name|XmlRootElement
argument_list|(
name|name
operator|=
literal|"sync_response"
argument_list|)
annotation|@
name|XmlAccessorType
argument_list|(
name|XmlAccessType
operator|.
name|FIELD
argument_list|)
specifier|public
class|class
name|SyncResponse
block|{
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"snapshot_position"
argument_list|)
specifier|public
name|long
name|snapshot_position
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"wal_append_position"
argument_list|)
specifier|public
name|long
name|wal_append_position
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"index_files"
argument_list|)
specifier|public
name|Set
argument_list|<
name|FileInfo
argument_list|>
name|index_files
init|=
operator|new
name|HashSet
argument_list|<
name|FileInfo
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"log_files"
argument_list|)
specifier|public
name|Set
argument_list|<
name|FileInfo
argument_list|>
name|log_files
init|=
operator|new
name|HashSet
argument_list|<
name|FileInfo
argument_list|>
argument_list|()
decl_stmt|;
annotation|@
name|XmlAttribute
argument_list|(
name|name
operator|=
literal|"append_log"
argument_list|)
specifier|public
name|String
name|append_log
decl_stmt|;
block|}
end_class

end_unit

