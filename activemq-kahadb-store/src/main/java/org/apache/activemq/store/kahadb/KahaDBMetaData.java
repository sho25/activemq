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
name|store
operator|.
name|kahadb
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataInput
import|;
end_import

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|DataOutput
import|;
end_import

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
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|kahadb
operator|.
name|disk
operator|.
name|journal
operator|.
name|Location
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|kahadb
operator|.
name|disk
operator|.
name|page
operator|.
name|Page
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
operator|.
name|kahadb
operator|.
name|disk
operator|.
name|page
operator|.
name|Transaction
import|;
end_import

begin_comment
comment|/**  * Interface for the store meta data used to hold the index value and other needed  * information to manage a KahaDB store instance.  */
end_comment

begin_interface
specifier|public
interface|interface
name|KahaDBMetaData
parameter_list|<
name|T
parameter_list|>
block|{
comment|/**      * Indicates that this meta data instance has been opened and is active.      */
specifier|public
specifier|static
specifier|final
name|int
name|OPEN_STATE
init|=
literal|2
decl_stmt|;
comment|/**      * Indicates that this meta data instance has been closed and is no longer active.      */
specifier|public
specifier|static
specifier|final
name|int
name|CLOSED_STATE
init|=
literal|1
decl_stmt|;
comment|/**      * Gets the Page in the store PageFile where the KahaDBMetaData instance is stored.      *      * @return the Page to use to start access the KahaDBMetaData instance.      */
name|Page
argument_list|<
name|T
argument_list|>
name|getPage
parameter_list|()
function_decl|;
comment|/**      * Sets the Page instance used to load and store the KahaDBMetaData instance.      *      * @param page      *        the new Page value to use.      */
name|void
name|setPage
parameter_list|(
name|Page
argument_list|<
name|T
argument_list|>
name|page
parameter_list|)
function_decl|;
comment|/**      * Gets the state flag of this meta data instance.      *      *  @return the current state value for this instance.      */
name|int
name|getState
parameter_list|()
function_decl|;
comment|/**      * Sets the current value of the state flag.      *      * @param value      *        the new value to assign to the state flag.      */
name|void
name|setState
parameter_list|(
name|int
name|value
parameter_list|)
function_decl|;
comment|/**      * Returns the Journal Location value that indicates that last recorded update      * that was successfully performed for this KahaDB store implementation.      *      * @return the location of the last successful update location.      */
name|Location
name|getLastUpdateLocation
parameter_list|()
function_decl|;
comment|/**      * Updates the value of the last successful update.      *      * @param location      *        the new value to assign the last update location field.      */
name|void
name|setLastUpdateLocation
parameter_list|(
name|Location
name|location
parameter_list|)
function_decl|;
comment|/**      * For a newly created KahaDBMetaData instance this method is called to allow      * the instance to create all of it's internal indices and other state data.      *      * @param tx      *        the Transaction instance under which the operation is executed.      *      * @throws IOException if an error occurs while creating the meta data structures.      */
name|void
name|initialize
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Instructs this object to load its internal data structures from the KahaDB PageFile      * and prepare itself for use.      *      * @param tx      *        the Transaction instance under which the operation is executed.      *      * @throws IOException if an error occurs while creating the meta data structures.      */
name|void
name|load
parameter_list|(
name|Transaction
name|tx
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Reads the serialized for of this object from the KadaDB PageFile and prepares it      * for use.  This method does not need to perform a full load of the meta data structures      * only read in the information necessary to load them from the PageFile on a call to the      * load method.      *      * @param in      *        the DataInput instance used to read this objects serialized form.      *      * @throws IOException if an error occurs while reading the serialized form.      */
name|void
name|read
parameter_list|(
name|DataInput
name|in
parameter_list|)
throws|throws
name|IOException
function_decl|;
comment|/**      * Writes the object into a serialized form which can be read back in again using the      * read method.      *      * @param out      *        the DataOutput instance to use to write the current state to a serialized form.      *      * @throws IOException if an error occurs while serializing this instance.      */
name|void
name|write
parameter_list|(
name|DataOutput
name|out
parameter_list|)
throws|throws
name|IOException
function_decl|;
block|}
end_interface

end_unit

