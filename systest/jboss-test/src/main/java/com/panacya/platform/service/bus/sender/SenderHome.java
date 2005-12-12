begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_package
package|package
name|com
operator|.
name|panacya
operator|.
name|platform
operator|.
name|service
operator|.
name|bus
operator|.
name|sender
package|;
end_package

begin_import
import|import
name|javax
operator|.
name|ejb
operator|.
name|CreateException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|ejb
operator|.
name|EJBHome
import|;
end_import

begin_import
import|import
name|java
operator|.
name|rmi
operator|.
name|RemoteException
import|;
end_import

begin_comment
comment|/**  * @author<a href="mailto:michael.gaffney@panacya.com">Michael Gaffney</a>  */
end_comment

begin_interface
specifier|public
interface|interface
name|SenderHome
extends|extends
name|EJBHome
block|{
name|com
operator|.
name|panacya
operator|.
name|platform
operator|.
name|service
operator|.
name|bus
operator|.
name|sender
operator|.
name|Sender
name|create
parameter_list|()
throws|throws
name|RemoteException
throws|,
name|CreateException
function_decl|;
block|}
end_interface

end_unit

