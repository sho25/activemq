begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
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
comment|/** * A Store is holds persistent containers *  * @version $Revision: 1.2 $ */
end_comment

begin_interface
specifier|public
interface|interface
name|Store
block|{
comment|/**      * close the store      * @throws IOException      */
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Force all writes to disk      * @throws IOException      */
specifier|public
name|void
name|force
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * empty all the contents of the store      * @throws IOException      */
specifier|public
name|void
name|clear
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * delete the store      * @return true if the delete was successful      * @throws IOException      */
specifier|public
name|boolean
name|delete
parameter_list|()
throws|throws
name|IOException
function_decl|;
comment|/**      * Checks if a MapContainer exists      * @param id      * @return new MapContainer      */
specifier|public
name|boolean
name|doesMapContainerExist
parameter_list|(
name|Object
name|id
parameter_list|)
function_decl|;
comment|/**      * Get a MapContainer with the given id - the MapContainer is created if needed      * @param id      * @return container for the associated id or null if it doesn't exist      * @throws IOException       */
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
comment|/**      * delete a container      * @param id      * @throws IOException      */
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
comment|/**      * Get a Set of call MapContainer Ids      * @return the set of ids      */
specifier|public
name|Set
name|getMapContainerIds
parameter_list|()
function_decl|;
comment|/**      * Checks if a ListContainer exists      * @param id      * @return new MapContainer      */
specifier|public
name|boolean
name|doesListContainerExist
parameter_list|(
name|Object
name|id
parameter_list|)
function_decl|;
comment|/**     * Get a ListContainer with the given id and creates it if it doesn't exist     * @param id     * @return container for the associated id or null if it doesn't exist  * @throws IOException      */
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
comment|/**     * delete a ListContainer     * @param id     * @throws IOException     */
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
comment|/**     * Get a Set of call ListContainer Ids     * @return the set of ids     */
specifier|public
name|Set
name|getListContainerIds
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

