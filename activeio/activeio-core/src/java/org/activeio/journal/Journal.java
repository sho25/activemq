begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/**  *  * Copyright 2004 The Apache Software Foundation  *  * Licensed under the Apache License, Version 2.0 (the "License");  * you may not use this file except in compliance with the License.  * You may obtain a copy of the License at  *  * http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|activeio
operator|.
name|journal
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
name|org
operator|.
name|activeio
operator|.
name|packet
operator|.
name|Packet
import|;
end_import

begin_comment
comment|/**  * A Journal is a record logging Interface that can be used to implement   * a transaction log.    *   *   * This interface was largely extracted out of the HOWL project to allow   * ActiveMQ to switch between different Journal implementations verry easily.   *   * @version $Revision: 1.1 $  */
end_comment

begin_interface
specifier|public
interface|interface
name|Journal
block|{
comment|/** 	 * Writes a {@see Packet} of  data to the journal.  If<code>sync</code> 	 * is true, then this call blocks until the data has landed on the physical  	 * disk.  Otherwise, this enqueues the write request and returns. 	 *  	 * @param record - the data to be written to disk. 	 * @param sync - If this call should block until the data lands on disk. 	 *  	 * @return RecordLocation the location where the data will be written to on disk. 	 *  	 * @throws IOException if the write failed. 	 * @throws IllegalStateException if the journal is closed. 	 */
specifier|public
name|RecordLocation
name|write
parameter_list|(
name|Packet
name|packet
parameter_list|,
name|boolean
name|sync
parameter_list|)
throws|throws
name|IOException
throws|,
name|IllegalStateException
function_decl|;
comment|/** 	 * Reads a previously written record from the journal.  	 *   	 * @param location is where to read the record from. 	 *  	 * @return the data previously written at the<code>location</code>. 	 *  	 * @throws InvalidRecordLocationException if<code>location</code> parameter is out of range.   	 *         It cannot be a location that is before the current mark.  	 * @throws IOException if the record could not be read. 	 * @throws IllegalStateException if the journal is closed. 	 */
specifier|public
name|Packet
name|read
parameter_list|(
name|RecordLocation
name|location
parameter_list|)
throws|throws
name|InvalidRecordLocationException
throws|,
name|IOException
throws|,
name|IllegalStateException
function_decl|;
comment|/** 	 * Informs the journal that all the journal space up to the<code>location</code> is no longer 	 * needed and can be reclaimed for reuse. 	 *  	 * @param location the location of the record to mark.  All record locations before the marked  	 * location will no longger be vaild.  	 *  	 * @param sync if this call should block until the mark is set on the journal. 	 *  	 * @throws InvalidRecordLocationException if<code>location</code> parameter is out of range.   	 *         It cannot be a location that is before the current mark.  	 * @throws IOException if the record could not be read. 	 * @throws IllegalStateException if the journal is closed. 	 */
specifier|public
specifier|abstract
name|void
name|setMark
parameter_list|(
name|RecordLocation
name|location
parameter_list|,
name|boolean
name|sync
parameter_list|)
throws|throws
name|InvalidRecordLocationException
throws|,
name|IOException
throws|,
name|IllegalStateException
function_decl|;
comment|/** 	 * Obtains the mark that was set in the Journal. 	 *  	 * @see read(RecordLocation location); 	 * @return the mark that was set in the Journal. 	 * @throws IllegalStateException if the journal is closed. 	 */
specifier|public
name|RecordLocation
name|getMark
parameter_list|()
throws|throws
name|IllegalStateException
function_decl|;
comment|/** 	 * Close the Journal.   	 * This is blocking operation that waits for any pending put opperations to be forced to disk. 	 * Once the Journal is closed, all other methods of the journal should throw IllegalStateException. 	 *  	 * @throws IOException if an error occurs while the journal is being closed. 	 */
specifier|public
specifier|abstract
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/** 	 * Allows you to get the next RecordLocation after the<code>location</code> that  	 * is in the journal. 	 *  	 * @param location the reference location the is used to find the next location. 	 * To get the oldest location available in the journal,<code>location</code>  	 * should be set to null. 	 *  	 *  	 * @return the next record location 	 *  	 * @throws InvalidRecordLocationException if<code>location</code> parameter is out of range.   	 *         It cannot be a location that is before the current mark.  	 * @throws IllegalStateException if the journal is closed. 	 */
specifier|public
specifier|abstract
name|RecordLocation
name|getNextRecordLocation
parameter_list|(
name|RecordLocation
name|location
parameter_list|)
throws|throws
name|InvalidRecordLocationException
throws|,
name|IOException
throws|,
name|IllegalStateException
function_decl|;
comment|/** 	 * Registers a<code>JournalEventListener</code> that will receive notifications from the Journal. 	 *  	 * @param listener object that will receive journal events. 	 * @throws IllegalStateException if the journal is closed. 	 */
specifier|public
specifier|abstract
name|void
name|setJournalEventListener
parameter_list|(
name|JournalEventListener
name|listener
parameter_list|)
throws|throws
name|IllegalStateException
function_decl|;
block|}
end_interface

end_unit

