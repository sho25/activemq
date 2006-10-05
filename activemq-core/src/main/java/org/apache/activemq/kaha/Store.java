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

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
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
comment|/**  * A Store is holds persistent containers  *   * @version $Revision: 1.2 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|Store
block|{
comment|/**      * Byte Marshaller      */
specifier|public
specifier|final
specifier|static
name|Marshaller
name|BytesMarshaller
init|=
operator|new
name|BytesMarshaller
argument_list|()
decl_stmt|;
comment|/**      * Object Marshaller      */
specifier|public
specifier|final
specifier|static
name|Marshaller
name|ObjectMarshaller
init|=
operator|new
name|ObjectMarshaller
argument_list|()
decl_stmt|;
comment|/**      * String Marshaller      */
specifier|public
specifier|final
specifier|static
name|Marshaller
name|StringMarshaller
init|=
operator|new
name|StringMarshaller
argument_list|()
decl_stmt|;
comment|/**      * close the store      *       * @throws IOException      */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Force all writes to disk      *       * @throws IOException      */
specifier|public
name|void
name|force
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * empty all the contents of the store      *       * @throws IOException      */
specifier|public
name|void
name|clear
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * delete the store      *       * @return true if the delete was successful      * @throws IOException      */
specifier|public
name|boolean
name|delete
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Checks if a MapContainer exists in the default container      *       * @param id      * @return new MapContainer      * @throws IOException      */
specifier|public
name|boolean
name|doesMapContainerExist
parameter_list|(
name|Object
name|id
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Checks if a MapContainer exists in the named container      *       * @param id      * @param containerName       * @return new MapContainer      * @throws IOException      */
specifier|public
name|boolean
name|doesMapContainerExist
parameter_list|(
name|Object
name|id
parameter_list|,
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Get a MapContainer with the given id - the MapContainer is created if needed      *       * @param id      * @return container for the associated id or null if it doesn't exist      * @throws IOException      */
specifier|public
name|MapContainer
name|getMapContainer
parameter_list|(
name|Object
name|id
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Get a MapContainer with the given id - the MapContainer is created if needed      *       * @param id      * @param containerName      * @return container for the associated id or null if it doesn't exist      * @throws IOException      */
specifier|public
name|MapContainer
name|getMapContainer
parameter_list|(
name|Object
name|id
parameter_list|,
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Get a MapContainer with the given id - the MapContainer is created if needed      *       * @param id      * @param containerName      * @param indexType       * @return container for the associated id or null if it doesn't exist      * @throws IOException      */
specifier|public
name|MapContainer
name|getMapContainer
parameter_list|(
name|Object
name|id
parameter_list|,
name|String
name|containerName
parameter_list|,
name|String
name|indexType
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * delete a container from the default container      *       * @param id      * @throws IOException      */
specifier|public
name|void
name|deleteMapContainer
parameter_list|(
name|Object
name|id
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * delete a MapContainer from the name container      *       * @param id      * @param containerName       * @throws IOException      */
specifier|public
name|void
name|deleteMapContainer
parameter_list|(
name|Object
name|id
parameter_list|,
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Get a Set of call MapContainer Ids      *       * @return the set of ids      * @throws IOException      */
specifier|public
name|Set
name|getMapContainerIds
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Checks if a ListContainer exists in the default container      *       * @param id      * @return new MapContainer      * @throws IOException      */
specifier|public
name|boolean
name|doesListContainerExist
parameter_list|(
name|Object
name|id
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Checks if a ListContainer exists in the named container      *       * @param id      * @param containerName       * @return new MapContainer      * @throws IOException      */
specifier|public
name|boolean
name|doesListContainerExist
parameter_list|(
name|Object
name|id
parameter_list|,
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Get a ListContainer with the given id and creates it if it doesn't exist      *       * @param id      * @return container for the associated id or null if it doesn't exist      * @throws IOException      */
specifier|public
name|ListContainer
name|getListContainer
parameter_list|(
name|Object
name|id
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Get a ListContainer with the given id and creates it if it doesn't exist      *       * @param id      * @param containerName      * @return container for the associated id or null if it doesn't exist      * @throws IOException      */
specifier|public
name|ListContainer
name|getListContainer
parameter_list|(
name|Object
name|id
parameter_list|,
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Get a ListContainer with the given id and creates it if it doesn't exist      *       * @param id      * @param containerName      * @param indexType       * @return container for the associated id or null if it doesn't exist      * @throws IOException      */
specifier|public
name|ListContainer
name|getListContainer
parameter_list|(
name|Object
name|id
parameter_list|,
name|String
name|containerName
parameter_list|,
name|String
name|indexType
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * delete a ListContainer from the default container      *       * @param id      * @throws IOException      */
specifier|public
name|void
name|deleteListContainer
parameter_list|(
name|Object
name|id
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * delete a ListContainer from the named container      *       * @param id      * @param containerName       * @throws IOException      */
specifier|public
name|void
name|deleteListContainer
parameter_list|(
name|Object
name|id
parameter_list|,
name|String
name|containerName
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Get a Set of call ListContainer Ids      *       * @return the set of ids      * @throws IOException      */
specifier|public
name|Set
name|getListContainerIds
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * @return the maxDataFileLength      */
specifier|public
name|long
name|getMaxDataFileLength
parameter_list|()
function_decl|;
comment|/**      * @param maxDataFileLength the maxDataFileLength to set      */
specifier|public
name|void
name|setMaxDataFileLength
parameter_list|(
name|long
name|maxDataFileLength
parameter_list|)
function_decl|;
comment|/**      * @see org.apache.activemq.kaha.IndexTypes      * @return the default index type      */
specifier|public
name|String
name|getIndexType
parameter_list|()
function_decl|;
comment|/**      * Set the default index type      * @param type      * @see org.apache.activemq.kaha.IndexTypes      */
specifier|public
name|void
name|setIndexType
parameter_list|(
name|String
name|type
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

