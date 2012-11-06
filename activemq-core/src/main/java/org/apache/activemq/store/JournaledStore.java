begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|activemq
operator|.
name|store
package|;
end_package

begin_comment
comment|/**  * @author<a href="http://hiramchirino.com">Hiram Chirino</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|JournaledStore
block|{
name|int
name|getJournalMaxFileLength
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

